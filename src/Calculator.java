import java.util.ArrayList;
import java.util.Date;


public class Calculator {

	public double FindAverage(ArrayList<Offer> offers){
		double sum = 0;
		double avg = 0;

		for (int i = 0; i < offers.size(); i++) {
			sum += offers.get(i).getOfferPrice();
		}

		avg = sum / offers.size();

		return avg;
	}
	
	public double GenerateFittedOffer(DetectionRegion detReg, int i, int j, ArrayList<Offer> offerHistory, Offer newOffer, int numberOfRounds){
		
		float b = GenerateBValue(detReg, i, j, offerHistory, newOffer, numberOfRounds);

		double offer = (double) (offerHistory.get(0).getOfferPrice() + (detReg.getCells()[i][j].getCellReservePrice() - offerHistory.get(0).getOfferPrice())* Math.pow((newOffer.getOfferTime().getTime() / detReg.getCells()[i][j].getCellDeadline().getTime()), b));
		return offer;
	}
	
	public double GenerateFittedOfferForGivenTime(DetectionRegion detReg, int i, int j, ArrayList<Offer> offerHistory, Offer newOffer, int numberOfRounds, Date time){
		
		float b = GenerateBValue(detReg, i, j, offerHistory, newOffer, numberOfRounds);
		
		double offer = (double)(offerHistory.get(0).getOfferPrice() + (detReg.getCells()[i][j].getCellReservePrice() - offerHistory.get(0).getOfferPrice())*Math.pow(time.getTime() / detReg.getCells()[i][j].getCellDeadline().getTime(), b));
		return offer;
	}
	
	public double generateGammaValue(Offer newOffer, double avgHistory, int numberOfRounds, double avgFitted, int i, int j, DetectionRegion detReg){

		double sumUP=0.0;
		double sumDown1=0.0;
		double sumDown2=0.0;
		
		for (int round = 1; round <= numberOfRounds; round++) {
			
			sumUP += (newOffer.getOfferPrice() - avgHistory)* (detReg.getCells()[i][j].getCellReservePrice() - avgFitted);
			sumDown1 += Math.pow((newOffer.getOfferPrice() - avgHistory), 2);
			sumDown2 += Math.pow(detReg.getCells()[i][j].getCellReservePrice() - avgFitted, 2); 			
		}
		
		double foundGamma= sumUP/Math.sqrt(sumDown1*sumDown2);
		return foundGamma;		
	}
	
	public Date GeneratedTimeForGivenFittedOffer(DetectionRegion detReg, int i, int j, ArrayList<Offer> offerHistory, Offer newOffer, int numberOfRounds, double offerPrice){
		
		float b = GenerateBValue(detReg, i, j, offerHistory, newOffer, numberOfRounds);
		
		Date resultTime = new Date((long) (Math.pow(((offerPrice - offerHistory.get(0).getOfferPrice()) / (detReg.getCells()[i][j].getCellReservePrice() - offerHistory.get(0).getOfferPrice())), 1/6) * (detReg.getCells()[i][j].getCellDeadline().getTime())));
		return resultTime;
	}
	
	private float GenerateBValue(DetectionRegion detReg, int i, int j, ArrayList<Offer> offerHistory, Offer newOffer, int numberOfRounds){
		float tpSum = 0;
		float t2Sum = 1;
		
		for(int k = 0; k < numberOfRounds; k++){
			
			float tStar = (float) Math.log(newOffer.getOfferTime().getTime() / detReg.getCells()[i][j].getCellDeadline().getTime());
			float pStar = (float) Math.log((offerHistory.get(0).getOfferPrice() - newOffer.getOfferPrice()) / (offerHistory.get(0).getOfferPrice() - detReg.getCells()[i][j].getCellReservePrice()));
	
			tpSum += tStar * pStar;
			t2Sum += Math.pow(tStar, 2);
		}
		
		return tpSum / t2Sum;
	}
	
	private double logOfBase(double base, double num) {
	    return Math.log(num) / Math.log(base);
	}
	
	public Offer GenerateNextOffer(DetectionRegion detReg, Agent thisAgent, int numberOfRows, int numberOfColumns, Offer newOffer, long stepSize){
		
		double base = 0.0;
		double value = 0.0;
		double BetaHat = 0.0;
		double Sum = 0.0;
		double BetaBar = 0.0;
		
		for(int i = 0; i < numberOfRows; i++){
			for(int j = 0; j < numberOfColumns; j++){
				
				base = (detReg.getCells()[i][j].getConcessionPoint().getConcessionPointTime().getTime() - newOffer.getOfferTime().getTime()) / (thisAgent.getDeadline().getTime() - newOffer.getOfferTime().getTime());
				value = (newOffer.getOfferPrice() - detReg.getCells()[i][j].getConcessionPoint().getConcessionPointPrice()) / (newOffer.getOfferPrice() - thisAgent.getReservePrice());
				BetaHat = logOfBase(base, value);
				
				Sum += detReg.getCells()[i][j].getProbability() / (1 + BetaHat);
			}
		}
		
		BetaBar = (1 / Sum) - 1;
		
		double offerPrice = (newOffer.getOfferPrice() + ((thisAgent.getReservePrice() - newOffer.getOfferPrice()) * (Math.pow((newOffer.getOfferTime().getTime() + stepSize - newOffer.getOfferTime().getTime()) / (thisAgent.getDeadline().getTime() - newOffer.getOfferTime().getTime()), BetaBar))));
		Date offerTime = new Date(newOffer.getOfferTime().getTime() + stepSize);
		
		Offer nextOffer = new Offer(offerPrice, offerTime, newOffer.getRoundNumber() + 1);
		return nextOffer;
	}	
	
	public Date GeneratePretendedDeadline(DetectionRegion detReg, int numberOfRows, int numberOfColumns, Offer newOffer){
		
		double maxProbability = 0.0;
		Date estimatedDeadlineOfOpponent = null;
		Cell[][] cells = detReg.getCells();
		
		for(int i = 0; i < numberOfRows; i++){
			for(int j = 0; j < numberOfColumns; j++){
				if(cells[i][j].getProbability() > maxProbability){
					maxProbability = cells[i][j].getProbability();
					estimatedDeadlineOfOpponent = cells[i][j].getCellDeadline();
				}
			}
		}
		
		long part1 = estimatedDeadlineOfOpponent.getTime() - newOffer.getOfferTime().getTime();
		double part2 = Math.pow((estimatedDeadlineOfOpponent.getTime() - newOffer.getOfferTime().getTime()), 2);
		long part3 = 4*estimatedDeadlineOfOpponent.getTime()*newOffer.getOfferTime().getTime();
		
		Date pretendedDeadline = new Date((long)(-part1 + Math.sqrt(part2 - part3))/2);
		return pretendedDeadline;
	}
}

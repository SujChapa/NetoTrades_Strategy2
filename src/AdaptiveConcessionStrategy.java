import java.util.ArrayList;
import java.util.Date;

public class AdaptiveConcessionStrategy {
	
	private Calculator calculator;
	
	public AdaptiveConcessionStrategy(Calculator calculator){
		this.calculator = calculator;
	}
	
	public void FindConcessionPoint(int numberOfRows, int numberOfColumns, DetectionRegion detReg, Offer newOffer, Agent thisAgent, long stepSize, int numberOfRounds, ArrayList<Offer> offerHistory){
		double concessionPrice = 0.0;
		Date concessionTime = null;
		ConcessionPoint newConcessionPoint;
		
		double RegressionValueAtTb = 0.0;
		double RegressionValueAtT0 = 0.0;
		
		for(int i = 0; i < numberOfRows; i++){
			for(int j = 0; i < numberOfColumns; j++){
				
				RegressionValueAtTb = calculator.GenerateFittedOfferForGivenTime(detReg, i, j, offerHistory, newOffer, numberOfRounds, thisAgent.getDeadline());
				RegressionValueAtT0 = calculator.GenerateFittedOfferForGivenTime(detReg, i, j, offerHistory, newOffer, numberOfRounds, newOffer.getOfferTime());
				
				if((detReg.getCells()[i][j].getCellDeadline().getTime() < thisAgent.getDeadline().getTime()) && (detReg.getCells()[i][j].getCellReservePrice() > newOffer.getOfferPrice())){
					
					concessionPrice = detReg.getCells()[i][j].getCellReservePrice();
					concessionTime = detReg.getCells()[i][j].getCellDeadline();
					
				}
				
				else if((detReg.getCells()[i][j].getCellDeadline().getTime() >= thisAgent.getDeadline().getTime()) && (detReg.getCells()[i][j].getCellReservePrice() >= newOffer.getOfferPrice())){
					
					if(RegressionValueAtTb > thisAgent.getReservePrice()){
						concessionPrice = thisAgent.getReservePrice();
						concessionTime = new Date(thisAgent.getDeadline().getTime() - stepSize);
					}
					
					else{
						concessionPrice = thisAgent.getReservePrice()*(0.9 + (0.01 * newOffer.getRoundNumber()));
						concessionTime = new Date(newOffer.getOfferTime().getTime() + stepSize);
					}
					
				}
				
				else if((detReg.getCells()[i][j].getCellDeadline().getTime() < thisAgent.getDeadline().getTime()) && (detReg.getCells()[i][j].getCellReservePrice() < newOffer.getOfferPrice())){
					
					if(RegressionValueAtT0 > thisAgent.getLastOffer().getOfferPrice()){
						Date intersectionTime = calculator.GeneratedTimeForGivenFittedOffer(detReg, i, j, offerHistory, newOffer, numberOfRounds, newOffer.getOfferPrice());
						concessionPrice = calculator.GenerateFittedOfferForGivenTime(detReg, i, j, offerHistory, newOffer, numberOfRounds, new Date(intersectionTime.getTime() - stepSize));
						concessionTime = new Date(intersectionTime.getTime() - stepSize);
					}
					
					else{
						concessionPrice = thisAgent.getLastOffer().getOfferPrice()*(1 + (0.01)*newOffer.getRoundNumber());
						concessionTime = new Date(thisAgent.getDeadline().getTime() - stepSize);
					}
					
				}
				
				else if((detReg.getCells()[i][j].getCellDeadline().getTime() >= thisAgent.getDeadline().getTime()) && (detReg.getCells()[i][j].getCellReservePrice() <= newOffer.getOfferPrice())){
					
					if(RegressionValueAtT0 < thisAgent.getLastOffer().getOfferPrice()){
						concessionPrice = thisAgent.getLastOffer().getOfferPrice()*(1 + (0.01)*newOffer.getRoundNumber());
						concessionTime = new Date(thisAgent.getDeadline().getTime() - stepSize);
					}
					
					else if((RegressionValueAtT0 > thisAgent.getLastOffer().getOfferPrice()) && (RegressionValueAtT0 < thisAgent.getReservePrice())){
						Date intersectionTime = calculator.GeneratedTimeForGivenFittedOffer(detReg, i, j, offerHistory, newOffer, numberOfRounds, newOffer.getOfferPrice());
						concessionPrice = calculator.GenerateFittedOfferForGivenTime(detReg, i, j, offerHistory, newOffer, numberOfRounds, new Date(intersectionTime.getTime() - stepSize));
						concessionTime = new Date(intersectionTime.getTime() - stepSize);
					}
					
					else{
						
						if(RegressionValueAtTb < thisAgent.getReservePrice()){
							concessionPrice = thisAgent.getReservePrice();
							concessionTime = new Date(thisAgent.getDeadline().getTime() - stepSize);
						}
						else{
							concessionPrice = thisAgent.getReservePrice()*(0.9 + (0.01 * newOffer.getRoundNumber()));
							concessionTime = new Date(newOffer.getOfferTime().getTime() + stepSize);
						}
					
					}					
				}
				
				newConcessionPoint = new ConcessionPoint(concessionPrice, concessionTime);
				detReg.getCells()[i][j].setConcessionPoint(newConcessionPoint);					
				
			}
		}
	}
	
	public Offer GenerateNextOffer(DetectionRegion detReg, Agent thisAgent, int numberOfRows, int numberOfColumns, Offer newOffer, long stepSize){
		return calculator.GenerateNextOffer(detReg, thisAgent, numberOfRows, numberOfColumns, newOffer, stepSize);
	}
}

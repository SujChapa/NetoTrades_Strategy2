import java.util.ArrayList;
import java.util.Date;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Calculator calculator = new Calculator();
		
		RegressionAnalyser regressionAnalyser = new RegressionAnalyser();	
		BayesianLearner bayesianLearner = new BayesianLearner();
		
		PretendingDeadlineGenerator pretendingDeadlineGenerator = new PretendingDeadlineGenerator(calculator);
		AdaptiveConcessionStrategy concessionStrategy = new AdaptiveConcessionStrategy(calculator);
		
		Date currentTime= new Date();

		Agent agent1 = new Agent(1650.00, new Date(currentTime.getTime()+36000000));
		
		ArrayList<Offer> offerHistory = new ArrayList<Offer>();
		
		DetectionRegion detReg = new DetectionRegion(itemPriceGuess/2, itemPriceGuess*2, currentTime, new Date(currentTime.getTime()+42000000));
		DetectionRegion.widthOfCellReservePrice = 50.0;
		DetectionRegion.widthOfCellDeadline = 
		DetectionRegion detReg = new DetectionRegion(itemPriceGuess/2, itemPriceGuess*2, currentTime, new Date(currentTime.getTime()+42000000));
		Cell[][] cells = new Cell[numberOfRows][numberOfColumns];
	}

}

import java.util.Date;

public class PretendingDeadlineGenerator {
	
	private Calculator calculator;
	
	public PretendingDeadlineGenerator(Calculator calculator){
		this.calculator = calculator;
	}
	
	public Date GeneratePretendedDeadline(DetectionRegion detReg, int numberOfRows, int numberOfColumns, Offer newOffer){
		return calculator.GeneratePretendedDeadline(detReg, numberOfRows, numberOfColumns, newOffer);
	}
}

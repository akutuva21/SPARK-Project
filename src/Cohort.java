import java.util.Comparator;

public class Cohort {

    private final double lambda; // lambda is the proposed growth rate
    private double maxtruetotal; // True Total is the J statistic (sensitivity + specificity - 1)
    private double error; // Stores the RMSE
    private double sensitivity; // Sensitivity = True Positive / (True Positive + False Negative)
    private double specificity; // Specificity = True Negative / (True Negative + False Positive)

    // Constructor function to initialize a cohort of patients
    public Cohort(double lambda, double sensitivity, double specificity, double error) {
        this.lambda = lambda;
        this.maxtruetotal = sensitivity + specificity - 1;
        this.error = error;
    }

    public double getlambda() {
        return lambda;
    }

    public double getMaxtruetotal() {
        return maxtruetotal;
    }

    public void setmaxtruetotal(double maxtruetotal) {
        this.maxtruetotal = maxtruetotal;
    }
    
    public double getSensitivity() { return sensitivity; }
    
    public void setSensitivity(double sensitivity) { this.sensitivity = sensitivity; }

    public double getSpecificity() { return specificity; }

    public void setSpecificity(double specificity) { this.specificity = specificity; }

    public double geterror() { return error; }

    public void seterror ( double error ) { this.error = error; }

    // Sort based on J statistic
    public static final Comparator<Cohort> TTSort = Comparator.comparingDouble(Cohort::getMaxtruetotal);

    // Sort based on RMSE value
    public static final Comparator<Cohort> ErrorSort = Comparator.comparingDouble(Cohort::geterror);

// --Commented out by Inspection START (6/14/2021 2:35 PM):
//    //@Override
//    public boolean equals(Cohort o) {
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        return maxtruetotal == o.maxtruetotal &&
//                error == o.error;
//    }
// --Commented out by Inspection STOP (6/14/2021 2:35 PM)
}

    //public static Comparator<alpha3> ErrorComp = new Comparator<alpha3>() { // not used
        //public int compare(alpha3 one, alpha3 other) {
            //return Double.compare(one.geterror(), other.geterror());
        //}
    //};


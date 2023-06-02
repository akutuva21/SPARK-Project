import java.util.ArrayList;

// Default object to store simulated patients and patient-specific values along with fractionation values of note
public class Patient {
    private double lambda; // Patient-specific tumor growth rate
    private double alpha; // Alpha value in linear-quadratic model of cell death
    // private double mu; // Scaling factor for proliferation saturation, Currently unused
    private double delta; // Death term, influenced by alpha and alpha-beta ratio
    private double fraction_size; // Size of each fraction at each administration
    private double min_dose; // Tracks the minimum dosage needed to ensure either LRC (8 weeks of treatment or while
    private double psi; // Proliferation Saturation Index of a given tumor
    private double K; // Carrying Capacity of a Tumor
    private ArrayList<ArrayList<Double>> data; // Stores time and volume information for a given tumor

    // private double num_fractions = cumul_dose / fraction_size;
    // double fraction_spacing; // inclusion of "vacation" days or weekends for instance

    // Generic constructor for patient object
    public Patient() { }

    // Getter function for lambda variable
    public double getlambda() {
        return lambda;
    }

    // Setter function for lambda variable
    public void setlambda(double lambda)
    {
        this.lambda = lambda;
    }
    
    // Getter function for alpha variable
    public double getalpha() {
        return alpha;
    }

    // Setter function for alpha variable
    public void setalpha(double alpha)
    {
        this.alpha = alpha;
    }

    // Setter function for mu variable (unused)
    // public void setmu(double mu) { this.mu = mu; }

    // Getter function for delta variable
    public double getdelta()
    {
        return delta;
    }

     // Setter function for delta variable
    public void setdelta(double delta)
    {
        this.delta = delta;
    }

    // Getter function for Fraction Size
    public double getFractionSize()
    {
        return fraction_size;
    }

    // Setter function for Fraction Size
    public void setFractionSize(double fraction_size)
    {
        this.fraction_size = fraction_size;
    }

    // Setter function for Minimum Dose Required
    public void setCumulDose() {
        // Cumulative amount of radiation administered (66-70 Gy for H&N Cancer as per ARO)
    }

    // Getter function for Minimum Dose Required
    public double getMinDose()
    {
        return min_dose;
    }

    // Setter function for Minimum Dose Required
    public void setMinDose(double min_dose)
    {
        this.min_dose = min_dose;
    }

    // Getter function for patient PSI
    public double getPSI()
    {
        return psi;
    }

    // Setter function for patient PSI
    public void setPSI(double psi)
    {
        this.psi = psi;
    }

    // Alternative setter function for patient PSI based on tumor volume and carrying capacity
    public void setPSI(double v0, double k)
    {
        psi = v0 / k;
    }

    // Getter function for patient time and volume information
    public ArrayList<ArrayList<Double>> getData() {
        return data;
    }

    // Setter function for patient time and volume information
    public void setData(ArrayList<ArrayList<Double>> data)
    {
        this.data = data;
    }

    // Getter function for Carrying Capacity, K
    public double getK ()
    {
        return K;
    }

    // Setter function for carrying capacity, K
    public void setK (double K)
    {
        this.K = K;
    }
}

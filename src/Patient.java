import java.util.ArrayList;

// Default object to store simulated patients and patient-specific values along with fractionation values of note
public class Patient {
    private double lambda; // Patient-specific tumor growth rate
    private double alpha; // Alpha value in linear-quadratic model of cell death
    private double mu; // Scaling factor for proliferation saturation, Currently unused
    private double delta; // Death term, influenced by alpha and alpha-beta ratio
    private double alpha_beta_ratio; // Ratio of Alpha and Beta terms in linear-quadratic model of cell death
    private double fraction_size; // Size of each fraction at each administration
    private double cumul_dose; // Cumulative amount of radiation administered (66-70 Gy for H&N Cancer as per ARO)
    private double min_dose; // Tracks the minimum dosage needed to ensure either LRC (8 weeks of treatment or while
                                // number of doses <= maximum dose / fraction size)
    private double cumul_time; // Tracks total time taken to ensure minimum dose administered
    private double fraction_freq; // Tracks the number of fractions administered per day
    private double psi; // Proliferation Saturation Index of a given tumor
    private double K; // Carrying Capacity of a Tumor
    private double v0; // Starting Volume of a Tumor (Normalized to 100%)
    
    private ArrayList<ArrayList<Double>> data; // Stores time and volume information for a given tumor

    private boolean ExceedsOne; // Checks that if anytime, the PSI of a given tumor > 1 (Volume > Carrying Capacity)

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
    public void setmu(double mu) { this.mu = mu; }

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

    // Getter function for Alpha / Beta Ratio in LQ Death Term
    public double getabratio()
    {
        return alpha_beta_ratio;
    }

    // Setter function for Alpha / Beta Ratio in LQ Death Term
    public void setabratio(double alpha_beta_ratio)
    {
        this.alpha_beta_ratio = alpha_beta_ratio;
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

    // Getter function for Cumulative Dose Required
    public double getCumulDose()
    {
        return cumul_dose;
    }

    // Setter function for Minimum Dose Required
    public void setCumulDose(double cumul_dose) { this.cumul_dose = cumul_dose; }

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

    // Getter function for Cumulative Time Needed
    public double getCumulTime()
    {
        return cumul_time;
    }

    // Setter function for Cumulative Time Needed
    public void setCumulTime(double cumul_time)
    {
        this.cumul_time = cumul_time;
    }

    // Getter function for Fraction Frequency
    public double getFractionFreq()
    {
        return fraction_freq;
    }

    // Setter function for Cumulative Time Needed
    public void setFractionFreq(double fraction_freq)
    {
        this.fraction_freq = fraction_freq;
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

    // Optional Getter function for Exceeds One Variable (Whether PSI Exceeds 1)
    public boolean getExceedsOne()
    {
        return ExceedsOne;
    }

    // Optional Setter function for Exceeds One Variable (Whether PSI Exceeds 1)
    public void setExceedsOne(boolean ExceedsOne)
    {
        this.ExceedsOne = ExceedsOne;
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

    // Getter function for Initial Tumor Volume
    public double getV0 ()
    {
        return v0;
    }

    // Setter function for Initial Tumor Volume
    public void setV0(double v0)
    {
        this.v0 = v0;
    }
}

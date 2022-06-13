import java.util.ArrayList;

public class Patient {
    private double lambda;
    private double alpha;
    private double mu;
    private double delta;
    private double abratio;
    private double fraction_size;
    private double cumul_dose; // 66-70 as per ARO
    private double min_dose;
    private double cumul_time;
    private double fraction_freq;
    private double psi;
    private double k;
    private double v0;
    
    private ArrayList<ArrayList<Double>> data;

    private boolean exceeds_one;

    // private double num_fractions = cumul_dose / fraction_size;
    // double fraction_spacing; // inclusion of "vacation" days or weekends for instance

    public Patient() { }

    public double getlambda() {
        return lambda;
    }

    public void setlambda(double lambda)
    {
        this.lambda = lambda;
    }
    
    public double getalpha() {
        return alpha;
    }

    public void setalpha(double alpha)
    {
        this.alpha = alpha;
    }

    public double getmu() {
        return mu;
    }

    public void setmu(double mu) { this.mu = mu; }

    public double getdelta()
    {
        return delta;
    }

    public void setdelta(double delta)
    {
        this.delta = delta;
    }

    public double getabratio()
    {
        return abratio;
    }

    public void setabratio(double abratio)
    {
        this.abratio = abratio;
    }

    public double getfraction_size()
    {
        return fraction_size;
    }

    public void setfraction_size(double fraction_size)
    {
        this.fraction_size = fraction_size;
    }

    public double getcumul_dose()
    {
        return cumul_dose;
    }

    public void setcumul_dose(double cumul_dose)
    {
        this.cumul_dose = cumul_dose;
        this.min_dose = cumul_dose;
    }

    public double getmin_dose()
    {
        return min_dose;
    }

    public void setmin_dose(double min_dose)
    {
        this.min_dose = min_dose;
    }

    public double getcumul_time()
    {
        return cumul_time;
    }

    public void setcumul_time(double cumul_time)
    {
        this.cumul_time = cumul_time;
    }

    public double getfraction_freq()
    {
        return fraction_freq;
    }

    public void setfraction_freq(double fraction_freq)
    {
        this.fraction_freq = fraction_freq;
    }

    public double getpsi()
    {
        return psi;
    }

    public void setpsi(double psi)
    {
        this.psi = psi;
    }

    public void setpsi(double v0, double k)
    {
        psi = v0 / k;
    }

    public ArrayList<ArrayList<Double>> getdata() {
        return data;
    }

    public void setdata(ArrayList<ArrayList<Double>> data)
    {
        this.data = data;
    }

    public boolean getExceeds_one()
    {
        return exceeds_one;
    }

    public void setExceeds_one(boolean exceeds_one)
    {
        this.exceeds_one = exceeds_one;
    }

    public double get_k()
    {
        return k;
    }

    public void set_k(double k)
    {
        this.k = k;
    }

    public double get_v0()
    {
        return v0;
    }

    public void set_v0(double v0)
    {
        this.v0 = v0;
    }

    /*public boolean equals(Patient o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return alpha == o.alpha &&
                mu == o.mu &&
                delta == o.delta;
    }*/
}

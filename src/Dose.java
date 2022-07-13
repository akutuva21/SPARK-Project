import java.util.ArrayList;
import java.util.Random;

// Stores general dose and other fractionation-related values
public class Dose {
    private static final double fraction_size = 2; // // Assumes a fraction size of 2 Gy
    private static final double percent_decr = 1 - 0.322; // Assumes a 32.2% decrease in tumor volume is needed to achieve LRC
    static double v0 = 100; // Assumes initial normalized tumor volume is at 100%
    private static final double ab_ratio = 10; // constant ratio of alpha / beta in gamma calculation
    private static final double max_dose = 120.0; // maximum dose to be administered to patients
    private static final double delta_t = 1.0 / 24.0; // Assumes change in time is hourly
    private static final double max_time = 8 * 7; // Assumes a max time of 8 weeks of treatment
    private static final double pretreat_time = 7; // Assumes a week between pretreatment scans and start of treatment

    // Checks if the given time is found in the array of fractionation times
    public static boolean ArrayCheck(double t, double delta_t, ArrayList<Double> hour) {
        for (double h : hour)
        {
            if (((int) (t / delta_t) - h) % (1 / delta_t) == 0)
            {
                return true;
            }
        }
        return false;
    }

    // Samples values from a boxplot distribution given q1, median, q3, min, and max
    // Taken from https://stackoverflow.com/a/28962994
    static public double next(Random rnd, double median, double q1, double q3, double min, double max)
    {
        double d = -3;
        while (d > 2.698 || d < -2.698) { // excludes values outside of range
            d = rnd.nextGaussian();
        }
        if (Math.abs(d) < 0.6745) {
            if (d < 0) {
                return median - (median - q1) / 0.6745 * (-d);  // 2nd quartile
            } else {
                return median + (q3 - median) / 0.6745 * d;  // 3rd quartile
            }
        } else {
            if (d < 0) {
                return q1 - (q1 - min) / (2.698 - 0.6745) * ((-d) - 0.6745);  // 1st quartile
            } else {
                return q3 + (max - q3) / (2.698 - 0.6745) * (d - 0.6745);  // 4th quartile
            }
        }
    }

    // Continues setting up patients and filters based on cumulative dose criteria, conducts pretreatment if indicated
    public static void cumulDose(ArrayList<Patient> allpts, int numpatients, ArrayList<Double> hour, boolean direct, boolean indirect, boolean pretreat, boolean psi_check, Random r, boolean include_k, boolean include_psi, boolean include_dv)
    {
        double psi = -1; double lambda = 0; double alpha = 0; double delta = 0;
        int i = 0; // Tracks Patient #
        
        while (i != numpatients) {
            int size = allpts.size(); // tracks current number of patients created
            ArrayList<ArrayList<Double>> data = new ArrayList<>();
            Patient p = new Patient();

            for (int n = 0; n < 5; n++)
                data.add(new ArrayList<>()); // appends time, volume, k_vals, psi_vals, dv_vals vectors
            p.setCumulDose(max_dose);

            boolean linear = !true; // Assumes a linear relation between fraction size and delta
            boolean logistic = !true; // Assumes a logistic relation between fraction size and delta
            boolean lq = !true; // Assumes a linear-quadratic relation between fraction size and delta
            boolean no_change = true; // Assumes no relation between fraction size and delta

            if (direct)
            {
                psi = next(r, 2.54/2.93, 2.19/2.93, 2.76/2.93, 1.62/2.93, 2.86/2.93);
                lambda = 0.07;
                alpha = Math.abs(r.nextGaussian() * 0.02 + 0.09);
            }
            if (indirect)
            {
                psi = next(r, 3.7/4.11, 3.03/4.11, 3.92/4.11, 2.27/4.11, 4.08/4.11);
                lambda = next(r, 0.49/3.72 * 0.6, 0.31/3.72 * 0.6, 0.93/3.72 * 0.6, 0.31/3.72 * 0.6, 1.31/3.72 * 0.6);

                if (no_change)
                {
                    lambda = 0.49/3.72 * 0.6; // median lambda
                    delta = next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
                }
                else if (linear)
                {
                    //delta = next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
                    delta = 0.1 * 1.45/4.31;

                    double ratio = 2.0 / delta; // median delta at 2 Gy
                    lambda = 0.49/3.72 * 0.6; // median lambda
                    delta = fraction_size / ratio; // scales delta based on the provided fraction size
                }
                else if (logistic)
                {
                    double y = 0.1 * 1.45/4.31; // median delta
                    double x = 2; // dose (x)
                    // C derived from inverse of logistic equation (delta = e^d / (e^d + c) - 1 / (1 + c))
                    double v = -Math.exp(x) * y - y + Math.exp(x) - 1;
                    double sqrt = Math.sqrt(-2 * Math.exp(x) * Math.pow(y, 2) + Math.exp(2 * x) * Math.pow(y, 2) + Math.pow(y, 2) - 2 * Math.exp(2 * x) * y + 2 * y - 2 * Math.exp(x) + Math.exp(2 * x) + 1);
                    double C1 = (v + sqrt) / (2 * y); // c1 = 181.48
                    double C2 = (v - sqrt) / (2 * y); // c2 = 0.041

                    delta = Math.exp(fraction_size) / (Math.exp(fraction_size) + C1) - 1/(1 + C1);
                    lambda = 0.49/3.72 * 0.6; // median lambda
                }
                else if (lq)
                {
                    double ab_ratio = 10;

                    delta = 0.1 * 1.45/4.31;
                    double ref_dose = 2;
                    double a = Math.log(1 - delta) / (-ref_dose - Math.pow(ref_dose, 2) / ab_ratio);
                    delta = 1 - Math.exp(-a * fraction_size - (a/ab_ratio) * Math.pow(fraction_size, 2));
                    lambda = 0.49/3.72 * 0.6; // median lambda
                }

                // delta = 0.07;
                // psi = 0.7;
            }
            doseHelper(allpts, p, hour, data, psi, alpha, delta, lambda, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv);
            if (allpts.size() == (size + 1)) i++;
        }
    }

    public static void gridSearch(ArrayList<Patient> allpts, ArrayList<Double> hour, boolean direct, boolean indirect, boolean pretreat, boolean psi_check, boolean include_k, boolean include_psi, boolean include_dv) {
        double psi; double alpha = 0; double delta = 0;
        double lambda = 0.07; // constant growth rate
        double[] alpha_range = {
                0.06,
                0.14
        }; // Develops a range of alpha values to be tested
        double[] delta_range = {
                0.01,
                0.09
        }; // Develops a range of delta values to be tested
        double[] psi_range = {
                0.6,
                1
        }; // Develops a range of PSI values to be tested

        double incr; // Determines level of incrementation in values for direct/indirect cell kill
        if (direct) incr = 0.0005;
        else incr = 0.0005;
        double psi_incr = 0.005;

        double[] range;
        if (direct) range = alpha_range;
        else range = delta_range;

        for (double x = range[0]; x <= range[1]; x += incr) {
            if (indirect) delta = x;
            if (direct) alpha = x;
            for (psi = psi_range[0]; psi <= psi_range[1]; psi += psi_incr) {
                Patient a = new Patient();
                ArrayList<ArrayList<Double>> data = new ArrayList<>();
                for (int n = 0; n < 5; n++)
                    data.add(new ArrayList<>()); // time, volume, k_vals, psi_vals, dv_vals

                doseHelper(allpts, a, hour, data, psi, alpha, delta, lambda, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv);
            }
        }
    }

    // Helper function to cumulDose function and Grid Search
    public static void doseHelper(ArrayList<Patient> allpts, Patient a, ArrayList<Double> hour, ArrayList<ArrayList<Double>> data, double psi, double alpha, double delta, double lambda, boolean direct, boolean indirect, boolean pretreat, boolean psi_check, boolean include_k, boolean include_psi, boolean include_dv)
    {
        double k = v0 / psi; // calculates k
        double gamma = 1 - Math.exp(-alpha * fraction_size - (alpha / ab_ratio) * Math.pow(fraction_size, 2)); // calculates gamma

        data.get(0).add(0.0);
        data.get(1).add(v0);
        if (include_k)
            data.get(2).add(k);
        if (include_psi)
            data.get(3).add(psi);
        double end = v0;
        if (pretreat) {
            end = getPretreat(data, lambda, include_k, include_psi, include_dv, k); // conducts pretreatment if needed
        }

        double cumul_dose = doseCheck(k, end, lambda, gamma, delta, fraction_size, max_dose, indirect, direct, pretreat, hour, data, percent_decr, include_k, include_psi, include_dv, psi_check);
        if (!(cumul_dose == -1) && !(cumul_dose == -2)) // filters LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size)
        {
            initializePatient(fraction_size, v0, lambda, alpha, delta, data, a, k, cumul_dose, max_dose);
            if (allpts.size() % 100.0 == 0.0 && allpts.size() > 1)
                System.out.println("Completed: " + allpts.size());
            allpts.add(a);
        }
    }

    // Initializes a given patient with provided values
    public static void initializePatient(double fraction_size, double v0, double lambda, double alpha, double delta, ArrayList<ArrayList<Double>> data, Patient p, double k, double cumul_dose, double max_dose) {
        p.setK(k);
        p.setV0(v0);
        p.setPSI(v0, k);
        p.setCumulDose(max_dose);
        p.setMinDose(cumul_dose);
        p.setData(data);
        p.setlambda(lambda);
        p.setFractionSize(fraction_size);
        p.setdelta(delta);
        p.setalpha(alpha);
    }

    // Conducts pretreatment for a period of "pretreat_time" days if required
    public static double getPretreat(ArrayList<ArrayList<Double>> data, double lambda, boolean include_k, boolean include_psi, boolean include_dv, double k) {
        double end = k / (1 + ((k / v0) - 1) * Math.exp(-lambda * pretreat_time));
        data.get(0).add(pretreat_time);
        data.get(1).add(end); // simulating only growth from first time point as v(diagnosis)
        if (include_k)
            data.get(2).add(k);
        if (include_psi)
            data.get(3).add(end / k);
        if (include_dv)
            data.get(4).add(end - v0);
        return end;
    }

    // Simulates treatment accounting for LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size)
    public static double doseCheck(double k, double end, double lambda, double gamma, double delta, double fraction_size,
                                   double cumul_dose, boolean indirect, boolean direct, boolean pretreat,
                                   ArrayList<Double> hour, ArrayList<ArrayList<Double>> data, double percent_dec,
                                   boolean include_k, boolean include_psi, boolean include_dv, boolean psi_check) {
        double t = 0;
        int numdoses = 0;
        double start = end;
        while (t < max_time && numdoses <= cumul_dose / fraction_size) // filters by LRC / time constraints (if either missed, return -1)
        {
            if (direct && indirect && (end / k > 1) && psi_check) // returns "-2" if psi > 1 with both direct/indirect kill activated
                return -2;
            if (end / start <= percent_dec) // if LRC achieved, return minimum dose
                return numdoses * fraction_size;
            t += delta_t; // t increments by 1 hour
            double weekend = (int) (t / delta_t) % (7 / delta_t);
            if (weekend <= 120) // if not weekend
            {
                if (ArrayCheck(t, delta_t, hour)) // everyday at h hour
                {
                    if (direct) {
                        end -= gamma * end * (1 - (end / k)); // direct cell kill
                    }
                    if (indirect) {
                        k *= (1 - delta); // indirect cell kill
                    }
                    if (direct || indirect)
                        numdoses++; // increment number of doses
                }
            }
            end = k / (1 + ((k / end) - 1) * Math.exp(-lambda * delta_t)); // volume changes as per analytic solution
            data.get(0).add(t + (pretreat ? pretreat_time : 0)); // add extra pretreatment time if pretreatment assumed
            data.get(1).add(end); // volume
            if (include_k)
                data.get(2).add(k); // k
            if (include_psi)
                data.get(3).add(end / k); // psi
            if (include_dv)
                data.get(4).add(end - start); // dV
        }
        return -1;
    }
}

    /* // Return a random number from a list of doubles in an ArrayList (vector) - currently unused
    public static double getRandom(ArrayList<Double> array, Random r) {
        int rnd = r.nextInt(array.size());
        return array.get(rnd);
    } */

    /* // Function to approximate values near zero given a value f (actual - expected) and an epsilon range for error
    static boolean nearZero(double f, double epsilon) {
        return ((-epsilon < f) && (f < epsilon));
    } */
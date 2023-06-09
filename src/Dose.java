import java.util.ArrayList;

// Stores general dose and other fractionation-related values
public class Dose {
    private static final double percent_decr = 1 - 0.322; // Assumes a 32.2% decrease in tumor volume is needed to achieve LRC
    static double v0 = 100; // Assumes initial normalized tumor volume is at 100%
    private static final double ab_ratio = 10; // constant ratio of alpha / beta in gamma calculation
    private static final double max_dose = 120.0; // maximum dose to be administered to patients
    private static final double delta_t = 1.0 / 24.0; // Assumes change in time is hourly
    private static final double max_time = 8 * 7; // Assumes a max time of 8 weeks of treatment
    private static final double pretreat_time = 7; // Assumes a week between pretreatment scans and start of treatment

    /**
     * Checks if the given time is found in the array of fractionation times (supports hyperfractionation if hour contains multiple times)
     * @param t time in hours since v0
     * @param delta_t time step (default = 1 hour)
     * @param hour array of fractionation times (default = 6 AM)
     * @return boolean whether the given time is found in the array of fraction times (true = fraction time, false = not fraction time)
     */
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

    /**
     * Conducts a grid search over the given ranges of alpha, delta, lambda, psi, and fraction size
     * @param allpts ArrayList (vector) of patient objects
     * @param hour ArrayList of hour(s) of treatment (current set at 6 am only - can be modified for hyperfractionation)
     * @param direct boolean for DVR (Direct Volume Reduction)
     * @param indirect boolean for CCR (Indirect Carrying Capacity Reduction)
     * @param pretreat boolean whether an early growth period is used
     * @param psi_check boolean for in-silico testing to see whether V ever exceeds K (Direct + Indirect case)
     * @param include_k boolean for whether carrying capacity at each time point is stored
     * @param include_psi boolean for whether PSI at each time point is stored
     * @param include_dv boolean for whether changes in tumor volume (dV) at each time point is stored
     * @param lrc_filter boolean for whether to end if LRC is achieved (currently set at 32.2% decrease in tumor volume)
     * @param dose_filter boolean for whether to cap the number of doses at a maximum dosage
     * @param time_filter boolean for whether to cap the time at a maximum number of days/weeks of treatment
     * @param logistic_start boolean for whether to keep v0 at 100 (false - default) or start at 0.1 (true - logistic growth)
     * @param lambda_range range of lambda values to be tested, if one value provided then only that value is tested
     * @param alpha_range range of alpha values to be tested, if one value provided then only that value is tested
     * @param delta_range range of delta values to be tested, if one value provided then only that value is tested
     * @param psi_range range of psi values to be tested, if one value provided then only that value is tested
     * @param frac_range range of fractions to be tested, if one value provided then only that value is tested
     * @param incr_range stores the increments of each parameter
     */
    public static void gridSearch(ArrayList<Patient> allpts, ArrayList<Double> hour, boolean direct, boolean indirect, boolean pretreat, boolean psi_check, boolean include_k, boolean include_psi, boolean include_dv, boolean lrc_filter, boolean dose_filter, boolean time_filter, boolean logistic_start, double[] lambda_range, double[] alpha_range, double[] delta_range, double[] psi_range, double[] frac_range, double[] incr_range) {
        // initialize alpha and delta variables
        double alpha = 0; double delta = 0;

        if (direct && indirect) {
            System.out.println("This should not be active but if so, this is just confirming that both DVR" +
                    " and CCR are active.");
        } else if (direct) {
                delta_range = new double[0];
        } else if (indirect) {
            alpha_range = new double[0];
        } else {
            // Neither DVR nor CCR is true
            alpha_range = new double[0];
            delta_range = new double[0];
        }
        
        // If logistic_start is true, then the initial volume is set to be close to 0% (used to visualize pure Logistic Growth)
        if (logistic_start)
            v0 = 0.1;
        else
            v0 = 100;

        // Allows customization of the for loop depending on whether alpha/delta are being used and their ranges
        double startAlpha = alpha_range.length > 0 ? alpha_range[0] : 0;
        double endAlpha = alpha_range.length > 0 ? alpha_range[alpha_range.length - 1] : 0;
        double startDelta = delta_range.length > 0 ? delta_range[0] : 0;
        double endDelta = delta_range.length > 0 ? delta_range[delta_range.length - 1] : 0;

        // Conducts grid search over the given ranges
        for (double a = startAlpha; a <= endAlpha; a += (incr_range[0] != 0 ? incr_range[0] : 1)) {
            if (direct) alpha = a;
            for (double d = startDelta; d <= endDelta; d += (incr_range[1] != 0 ? incr_range[1] : 1)) {
                if (indirect) delta = d;
                for (double l = lambda_range[0]; l <= lambda_range[lambda_range.length - 1]; l += (incr_range[2] != 0 ? incr_range[2] : 1)) {
                    for (double psi = psi_range[0]; psi <= psi_range[psi_range.length - 1]; psi += (incr_range[3] != 0 ? incr_range[3] : 1)) {
                        for (double f = frac_range[0]; f <= frac_range[frac_range.length - 1]; f += (incr_range[4] != 0 ? incr_range[4] : 1)) {
                            
                            // Creates a new patient and data array for each combination of parameters
                            Patient p = new Patient();

                            // Creates an array to store each in-silico patient's time, volume, carrying capacity, PSI, and initial volume
                            ArrayList<ArrayList<Double>> data = new ArrayList<>();
                            for (int n = 0; n < 5; n++) {
                                data.add(new ArrayList<>());
                            }

                            // Conducts the dose calculations for each patient
                            doseHelper(allpts, p, hour, data, psi, alpha, delta, l, f, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv, lrc_filter, dose_filter, time_filter);
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper function to cumulDose function and Grid Search
     * @param allpts ArrayList (vector) of patient objects
     * @param a Patient object
     * @param hour ArrayList of hour(s) of treatment (current set at 6 am only - can be modified for hyperfractionation)
     * @param data An array to store each in-silico patient's time, volume, carrying capacity, PSI, and initial volume
     * @param psi psi value (equivalent to patient volume divided by the corresponding carrying capacity)
     * @param alpha alpha value (death parameter for DVR)
     * @param delta delta value (death parameter for CCR)
     * @param lambda lambda value (growth parameter)
     * @param fraction_size fraction size (in Gy) (default = 2 Gy)
     * @param direct boolean for DVR (Direct Volume Reduction)
     * @param indirect boolean for CCR (Indirect Carrying Capacity Reduction)
     * @param pretreat boolean whether an early growth period is used
     * @param psi_check boolean for in-silico testing to see whether V ever exceeds K (Direct + Indirect case)
     * @param include_k boolean for whether carrying capacity at each time point is stored
     * @param include_psi boolean for whether PSI at each time point is stored
     * @param include_dv boolean for whether changes in tumor volume (dV) at each time point is stored
     * @param lrc_filter boolean for whether to end if LRC is achieved (currently set at 32.2% decrease in tumor volume)
     * @param dose_filter boolean for whether to cap the number of doses at a maximum dosage
     * @param time_filter boolean for whether to cap the time at a maximum number of days/weeks of treatment
     */
    public static void doseHelper(ArrayList<Patient> allpts, Patient a, ArrayList<Double> hour, ArrayList<ArrayList<Double>> data, double psi, double alpha, double delta, double lambda, double fraction_size, boolean direct, boolean indirect, boolean pretreat, boolean psi_check, boolean include_k, boolean include_psi, boolean include_dv, boolean lrc_filter, boolean dose_filter, boolean time_filter)
    {
        double k = v0 / psi; // calculates carrying capacity based on initial volume and psi
        
        // calculates gamma based on alpha and fraction size for DVR
        double gamma = 1 - Math.exp(-alpha * fraction_size - (alpha / ab_ratio) * Math.pow(fraction_size, 2));

        // Adds the initial values to the data array
        data.get(0).add(0.0);
        data.get(1).add(v0);
        if (include_k)
            data.get(2).add(k);
        if (include_psi)
            data.get(3).add(psi);
        if (include_dv)
            data.get(4).add(v0);
        double end = v0;
        if (pretreat) {
            end = getPretreat(data, lambda, include_k, include_psi, include_dv, k); // conducts pretreatment if needed
        }

        double cumul_dose = doseCheck(k, end, lambda, gamma, delta, fraction_size, indirect, direct, pretreat, hour, data, percent_decr, include_k, include_psi, include_dv, psi_check, lrc_filter, dose_filter, time_filter);
        if (!(cumul_dose == -1)) // filters LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size) and PSI exceeding one
        {
            initializePatient(fraction_size, v0, lambda, alpha, delta, data, a, k, cumul_dose);
            if (allpts.size() % 100.0 == 0.0 && allpts.size() > 1)
                System.out.println("Completed: " + allpts.size());
            allpts.add(a);
        }
    }

    /**
     * Initializes a given patient with provided values
     * @param fraction_size fraction size (in Gy) (default = 2 Gy)
     * @param v0 normalized initial tumor volume (default = 100, otherwise = 1 if logistic_start)
     * @param lambda lambda value (growth parameter)
     * @param alpha alpha value (death parameter for DVR)
     * @param delta delta value (death parameter for CCR)
     * @param data An array to store each in-silico patient's time, volume, carrying capacity, PSI, and initial volume
     * @param p Patient object
     * @param k Expected Tumor Carrying capacity
     * @param cumul_dose Cumulative dose at the end of treatment (in Gy) (calculated by doseCheck function)
     */
    public static void initializePatient(double fraction_size, double v0, double lambda, double alpha, double delta, ArrayList<ArrayList<Double>> data, Patient p, double k, double cumul_dose) {
        p.setK(k);
        p.setPSI(v0, k);
        p.setCumulDose();
        p.setMinDose(cumul_dose);
        p.setData(data);
        p.setlambda(lambda);
        p.setFractionSize(fraction_size);
        p.setdelta(delta);
        p.setalpha(alpha);
    }

    /**
     * Conducts pretreatment for a period of "pretreat_time" days if required
     * @param data An array to store each in-silico patient's time, volume, carrying capacity, PSI, and initial volume
     * @param lambda lambda value (growth parameter)
     * @param include_k boolean for whether carrying capacity at each time point is stored
     * @param include_psi boolean for whether PSI at each time point is stored
     * @param include_dv boolean for whether changes in tumor volume (dV) at each time point is stored
     * @param k Expected Tumor Carrying capacity
     * @return
     */
    public static double getPretreat(ArrayList<ArrayList<Double>> data, double lambda, boolean include_k, boolean include_psi, boolean include_dv, double k) {
        // Uses the analytical solution to the logistic equation to calculate the volume at the end of pretreatment
        double end = k / (1 + ((k / v0) - 1) * Math.exp(-lambda * pretreat_time));
        
        // simulating only growth from first time point as v(diagnosis) = v0
        data.get(0).add(pretreat_time);
        data.get(1).add(end);
        if (include_k)
            data.get(2).add(k);
        if (include_psi)
            data.get(3).add(end / k);
        if (include_dv)
            data.get(4).add(end - v0);
        return end;
    }

    /**
     * Simulates treatment accounting for LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size)
     * @param k Expected Tumor Carrying capacity
     * @param end Expected Normalized Volume at the end of each time step
     * @param lambda lambda value (growth parameter)
     * @param gamma gamma value (death parameter calculated by alpha and fraction size for DVR)
     * @param delta delta value (death parameter for CCR)
     * @param fraction_size fraction size (in Gy) (default = 2)
     * @param indirect boolean for CCR (Indirect Carrying Capacity Reduction)
     * @param direct boolean for DVR (Direct Volume Reduction)
     * @param pretreat boolean whether an early growth period is used
     * @param hour ArrayList of hour(s) of treatment (current set at 6 am only - can be modified for hyperfractionation)
     * @param data An array to store each in-silico patient's time, volume, carrying capacity, PSI, and initial volume
     * @param percent_dec Percent decrease in tumor volume needed to achieve LRC (default = 0.322)
     * @param include_k boolean for whether carrying capacity at each time point is stored
     * @param include_psi boolean for whether PSI at each time point is stored
     * @param include_dv boolean for whether changes in tumor volume (dV) at each time point is stored
     * @param psi_check boolean used in in-silico testing to see whether V ever exceeds K (DVR + CCR Case)
     * @param lrc_filter boolean for whether to end if LRC is achieved (currently set at 32.2% decrease in tumor volume)
     * @param dose_filter boolean for whether to cap the number of doses at a maximum dosage
     * @param time_filter boolean for whether to cap the time at a maximum number of days/weeks of treatment
     * @return the maximum dose (in Gy) that can be given to a patient, given boolean restrictions in place (e.g. LRC, maximum dose, etc.)
     */
    public static double doseCheck(double k, double end, double lambda, double gamma, double delta, double fraction_size,
                                   boolean indirect, boolean direct, boolean pretreat, ArrayList<Double> hour,
                                   ArrayList<ArrayList<Double>> data, double percent_dec, boolean include_k,
                                   boolean include_psi, boolean include_dv, boolean psi_check, boolean lrc_filter,
                                   boolean dose_filter, boolean time_filter) {
        double t = 0;
        int numdoses = 0;
        double start = end;

        // filters by LRC / time constraints (if either missed, return -1)
        while (true)
        {
            // either filters by dose or returns the maximum dose
            if (numdoses * fraction_size > max_dose)
            {
                if (dose_filter)
                    break;
                else return max_dose;
            }

            // returns "-1" if psi > 1 with both DVR/CCR active
            if (direct && indirect && (end / k > 1)) {
                if (psi_check)
                    break;
            }

            // either filters by time or returns the dose corresponding to the maximum time
            if (t >= max_time) {
                if (time_filter)
                    break;
                else return numdoses * fraction_size;
            }

            // if LRC achieved, return minimum dose
            if (end / start <= percent_dec)
            {
                if (lrc_filter)
                    return numdoses * fraction_size;
            }

            t += delta_t; // t increments by 1 hour
            double weekend = (int) (t / delta_t) % (7 / delta_t);
            if (weekend <= 120) // if not weekend
            {
                if (ArrayCheck(t, delta_t, hour)) // everyday at h hour
                {
                    if (direct) {
                        end -= gamma * end * (1 - (end / k)); // DVR
                    }
                    if (indirect) {
                        k *= (1 - delta); // CCR
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
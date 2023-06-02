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

    // Checks if the given time is found in the array of fractionation times
    /**
     * @param t
     * @param delta_t
     * @param hour
     * @return
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
     * @param allpts
     * @param hour
     * @param direct
     * @param indirect
     * @param pretreat
     * @param psi_check
     * @param include_k
     * @param include_psi
     * @param include_dv
     * @param lrc_filter
     * @param dose_filter
     * @param time_filter
     * @param volume_start
     * @param lambda_range
     * @param alpha_range
     * @param delta_range
     * @param psi_range
     * @param frac_range
     * @param incr_range
     */
    public static void gridSearch(ArrayList<Patient> allpts, ArrayList<Double> hour, boolean direct, boolean indirect, boolean pretreat, boolean psi_check, boolean include_k, boolean include_psi, boolean include_dv, boolean lrc_filter, boolean dose_filter, boolean time_filter, boolean volume_start, double[] lambda_range, double[] alpha_range, double[] delta_range, double[] psi_range, double[] frac_range, double[] incr_range) {
        double alpha = 0; double delta = 0;
        if (direct && indirect) {
            System.out.println("This should not be active but if so, this is just confirming that both direct" +
                    " and indirect cell kill are active.");
        } else if (direct) {
                delta_range = new double[0];
        } else if (indirect) {
            alpha_range = new double[0];
        } else {
            // Neither direct nor indirect is true
            alpha_range = new double[0];
            delta_range = new double[0];
        }
        if (volume_start)
            v0 = 0.1;
        else
            v0 = 100;

        double startAlpha = alpha_range.length > 0 ? alpha_range[0] : 0;
        double endAlpha = alpha_range.length > 0 ? alpha_range[alpha_range.length - 1] : 0;
        double startDelta = delta_range.length > 0 ? delta_range[0] : 0;
        double endDelta = delta_range.length > 0 ? delta_range[delta_range.length - 1] : 0;

        for (double a = startAlpha; a <= endAlpha; a += (incr_range[0] != 0 ? incr_range[0] : 1)) {
            if (direct) alpha = a;
            for (double d = startDelta; d <= endDelta; d += (incr_range[1] != 0 ? incr_range[1] : 1)) {
                if (indirect) delta = d;
                for (double l = lambda_range[0]; l <= lambda_range[lambda_range.length - 1]; l += (incr_range[2] != 0 ? incr_range[2] : 1)) {
                    for (double psi = psi_range[0]; psi <= psi_range[psi_range.length - 1]; psi += (incr_range[3] != 0 ? incr_range[3] : 1)) {
                        for (double f = frac_range[0]; f <= frac_range[frac_range.length - 1]; f += (incr_range[4] != 0 ? incr_range[4] : 1)) {
                            Patient p = new Patient();
                            ArrayList<ArrayList<Double>> data = new ArrayList<>();
                            for (int n = 0; n < 5; n++) {
                                data.add(new ArrayList<>());
                            }
                            doseHelper(allpts, p, hour, data, psi, alpha, delta, l, f, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv, lrc_filter, dose_filter, time_filter);
                        }
                    }
                }
            }
        }
    }

    // Helper function to cumulDose function and Grid Search
    /**
     * @param allpts
     * @param a
     * @param hour
     * @param data
     * @param psi
     * @param alpha
     * @param delta
     * @param lambda
     * @param fraction_size
     * @param direct
     * @param indirect
     * @param pretreat
     * @param psi_check
     * @param include_k
     * @param include_psi
     * @param include_dv
     * @param lrc_filter
     * @param dose_filter
     * @param time_filter
     */
    public static void doseHelper(ArrayList<Patient> allpts, Patient a, ArrayList<Double> hour, ArrayList<ArrayList<Double>> data, double psi, double alpha, double delta, double lambda, double fraction_size, boolean direct, boolean indirect, boolean pretreat, boolean psi_check, boolean include_k, boolean include_psi, boolean include_dv, boolean lrc_filter, boolean dose_filter, boolean time_filter)
    {
        double k = v0 / psi; // calculates k
        double gamma = 1 - Math.exp(-alpha * fraction_size - (alpha / ab_ratio) * Math.pow(fraction_size, 2)); // calculates gamma

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

    // Initializes a given patient with provided values
    /**
     * @param fraction_size
     * @param v0
     * @param lambda
     * @param alpha
     * @param delta
     * @param data
     * @param p
     * @param k
     * @param cumul_dose
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

    // Conducts pretreatment for a period of "pretreat_time" days if required
    /**
     * @param data
     * @param lambda
     * @param include_k
     * @param include_psi
     * @param include_dv
     * @param k
     * @return
     */
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
    /**
     * @param k
     * @param end
     * @param lambda
     * @param gamma
     * @param delta
     * @param fraction_size
     * @param indirect
     * @param direct
     * @param pretreat
     * @param hour
     * @param data
     * @param percent_dec
     * @param include_k
     * @param include_psi
     * @param include_dv
     * @param psi_check
     * @param lrc_filter
     * @param dose_filter
     * @param time_filter
     * @return
     */
    public static double doseCheck(double k, double end, double lambda, double gamma, double delta, double fraction_size,
                                   boolean indirect, boolean direct, boolean pretreat, ArrayList<Double> hour,
                                   ArrayList<ArrayList<Double>> data, double percent_dec, boolean include_k,
                                   boolean include_psi, boolean include_dv, boolean psi_check, boolean lrc_filter,
                                   boolean dose_filter, boolean time_filter) {
        double t = 0;
        int numdoses = 0;
        double start = end;
        while (true) // filters by LRC / time constraints (if either missed, return -1)
        {
            if (numdoses * fraction_size > max_dose)
            {
                if (dose_filter)
                    break;
                else return max_dose;
            }
            if (direct && indirect && (end / k > 1)) // returns "-1" if psi > 1 with both direct/indirect kill activated
                if (psi_check)
                    break;
            if (t >= max_time) {
                if (time_filter)
                    break;
                else return numdoses * fraction_size;
            }
            if (end / start <= percent_dec) // if LRC achieved, return minimum dose
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
import java.util.ArrayList;
import java.util.Random;

public class Dose {

    // Return a random number from a list of doubles in an ArrayList (vector)
    public static double getRandom(ArrayList<Double> array, Random r) {
        int rnd = r.nextInt(array.size());
        return array.get(rnd);
    }

    // Checks if the given time is found in the array of fractionation times
    public static boolean ArrayCheck(double t, double delta_t, ArrayList<Double> hour) {
        boolean check = false;
        for (double h : hour)
        {
            if (((int) (t / delta_t) - h) % (1 / delta_t) == 0)
            {
                check = true;
                break;
            }
        }
        return check;
    }

    // Creates patients and assigns them a minimum dose
    public static ArrayList<Patient> cumul_dose(ArrayList<Patient> allpts, boolean direct, boolean indirect, boolean pretreat, int trialnum, ArrayList<Double> hour, Random r)
    {
        for (int i = 0; i < trialnum; i++)
        {
            Patient p = new Patient();
            double max_dose = 120.0;
            p.setCumulDose(max_dose);
            //p.setmin_dose(max_dose);
            allpts.add(p);
        }
        return Cumulative_Dose_Patients(allpts, direct, indirect, pretreat, hour, r);
    }

    // Sets up the patients and simulates the initial growth period prior to RT
    public static ArrayList<Patient> Cumulative_Dose_Patients(ArrayList<Patient> allpts, boolean direct, boolean indirect, boolean pretreat, ArrayList<Double> hour, Random r)
    {
        double fraction_size; // d
        double fraction_freq = 1; // Assumes fractionation once a day
        double percent_decr = 1 - 0.322; // Assumes a 32.2% decrease in tumor volume is needed to achieve LRC
        double psi = -1; double k; double v0; double lambda = 0; double alpha = 0; double delta = 0;
        int scale = (int) Math.pow(10, 1); // Used for rounding
        boolean include_k = false; // Whether K values are stored (if modified over time)
        boolean include_psi = false; // Whether PSI values are stored (if modified over time)
        boolean include_dv = false; // Whether changes are volume are stored over time (if modified over time)

        int i = 0;
        v0 = 100; // Assumes initial normalized tumor volume is at 100%
        while (i != allpts.size()) {
            ArrayList<ArrayList<Double>> data = new ArrayList<>();
            for (int n = 0; n < 5; n++)
                data.add(new ArrayList<>()); // time, volume, k_vals, psi_vals, dv_vals
            fraction_size = 2; // Assumes a fraction size of 2 Gy
            allpts.get(i).setMinDose(allpts.get(i).getCumulDose());

            boolean linear = !true; // Assumes a linear relation between fraction size and delta
            boolean logistic = !true; // Assumes a logistic relation between fraction size and delta
            boolean lq = !true; // Assumes a linear-quadratic relation between fraction size and delta
            boolean no_change = true; // Assumes no relation between fraction size and delta
            double abratio = 10;

            if (direct)
            {
                psi = In_Silico.next(r, 2.54/2.93, 2.19/2.93, 2.76/2.93, 1.62/2.93, 2.86/2.93);
                lambda = 0.07;
                alpha = Math.abs(r.nextGaussian() * 0.02 + 0.09);
            }
            if (indirect)
            {
                psi = In_Silico.next(r, 3.7/4.11, 3.03/4.11, 3.92/4.11, 2.27/4.11, 4.08/4.11);
                lambda = In_Silico.next(r, 0.49/3.72 * 0.6, 0.31/3.72 * 0.6, 0.93/3.72 * 0.6, 0.31/3.72 * 0.6, 1.31/3.72 * 0.6);

                if (no_change)
                {
                    lambda = 0.49/3.72 * 0.6; // median lambda
                    delta = In_Silico.next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
                }
                else if (linear)
                {
                    //delta = In_Silico.next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
                    delta = 0.1 * 1.45/4.31;

                    double ratio = 2.0 / delta; // median delta at 2 Gy
                    lambda = 0.49/3.72 * 0.6; // median lambda
                    delta = fraction_size / ratio;
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

                delta = 0.07;
                psi = 0.7;
            }
            k = v0 / psi;

            double gamma = 1 - Math.exp(-alpha * fraction_size - (alpha / abratio) * Math.pow(fraction_size, 2));

            data.get(0).add(0.0);
            data.get(1).add(v0);
            if (include_k)
                data.get(2).add(k);
            if (include_psi)
                data.get(3).add(psi);
            double end = v0;
            if (pretreat)
            {
                end = k / (1 + ((k / v0) - 1) * Math.exp(-lambda * fraction_freq));
                data.get(0).add(fraction_freq);
                data.get(1).add(end); // simulating only growth from first time point as v(diagnosis)
                if (include_k)
                    data.get(2).add(k);
                if (include_psi)
                    data.get(3).add(end / k);
                if (include_dv)
                    data.get(4).add(end - v0);
            }

            double cumul_dose = Cumul_Dose_Check(k, end, lambda, gamma, delta, fraction_freq, fraction_size, allpts.get(i).getMinDose(), indirect, direct, pretreat, hour, data, percent_decr, include_k, include_psi, include_dv);
            if (cumul_dose != -1) // filters LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size)
            {
                allpts.get(i).setK(k);
                allpts.get(i).setV0(v0);
                allpts.get(i).setPSI(v0, k);
                allpts.get(i).setMinDose(cumul_dose);
                allpts.get(i).setData(data);
                allpts.get(i).setlambda(lambda);
                allpts.get(i).setFractionSize(fraction_size);
                allpts.get(i).setdelta(delta);
                allpts.get(i).setalpha(alpha);
                i++;
                if (i % (allpts.size() / 100.0) == 0.0)
                    System.out.println("Completed: " + i);
            }
        }
        return allpts;
    }

    // Simulates treatment accounting for LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size)
    public static double Cumul_Dose_Check(double k, double end, double lambda, double gamma, double delta, double fraction_freq, double fraction_size, double cumul_dose, boolean indirect, boolean direct, boolean pretreat, ArrayList<Double> hour, ArrayList<ArrayList<Double>> data, double percent_dec, boolean include_k, boolean include_psi, boolean include_dv) {
        double t = 0;
        int numdoses = 0;
        double delta_t = 1.0 / 24.0; // Assumes change in time is hourly
        double start = end;
        double maxtime = 8 * 7; // Assumes a max time of 8 weeks of treatment
        while (t < maxtime && numdoses <= cumul_dose / fraction_size) {
            if (end / start <= percent_dec)
            {
                return numdoses * fraction_size;
            }
            t += delta_t;
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
            end = k / (1 + ((k / end) - 1) * Math.exp(-lambda * delta_t));
            if (pretreat)
                data.get(0).add(t + fraction_freq); // time if pretreatment included
            else
                data.get(0).add(t); // time if pretreatment not included
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

    public static void Grid_Search(ArrayList<Patient> allpts, boolean direct, boolean indirect, boolean pretreat, ArrayList<Double> hour, Random r)
    {
        double fraction_size; // d
        double fraction_freq = 1; // Assumes fractionation once a day
        double percent_decr = 1 - 0.322; // Assumes a 32.2% decrease in tumor volume is needed to achieve LRC
        double k; double v0; double alpha = 0; double delta = 0;

        int scale = (int) Math.pow(10, 1);
        boolean include_k = false;
        boolean include_psi = false;
        boolean include_dv = false;

        v0 = 100; // Assumes initial normalized tumor volume is at 100%
        double lambda = 0.07; // constant growth rate
        double[] alpha_range = {0.06, 0.14}; // Develops a range of alpha values to be tested
        double[] delta_range = {0.01, 0.09}; // Develops a range of delta values to be tested
        double[] psi_range = {0.6, 1}; // Develops a range of PSI values to be tested

        double incr; // Determines level of incrementation in values for direct/indirect cell kill
        if (direct) incr = 0.0005;
        else incr = 0.001;
        double psi_incr = 0.005;

        double[] range;
        if (direct) range = alpha_range;
        else range = delta_range;

        for (double x = range[0]; x <= range[1]; x += incr) {
            if (indirect) delta = x;
            if (direct) alpha = x;
            for (double psi = psi_range[0]; psi <= psi_range[1]; psi += psi_incr) {
                Patient a = new Patient();
                ArrayList<ArrayList<Double>> data = new ArrayList<>();
                for (int n = 0; n < 5; n++)
                    data.add(new ArrayList<>()); // time, volume, k_vals, psi_vals, dv_vals
                fraction_size = 2;

                double max_dose = 120.0;
                a.setCumulDose(max_dose);

                double abratio = 10;
                k = v0 / psi;

                double gamma = 1 - Math.exp(-alpha * fraction_size - (alpha / abratio) * Math.pow(fraction_size, 2));

                data.get(0).add(0.0);
                data.get(1).add(v0);
                if (include_k)
                    data.get(2).add(k);
                if (include_psi)
                    data.get(3).add(psi);
                double end = v0;
                if (pretreat) {
                    end = k / (1 + ((k / v0) - 1) * Math.exp(-lambda * fraction_freq));
                    data.get(0).add(fraction_freq);
                    data.get(1).add(end); // simulating only growth from first time point as v(diagnosis)
                    if (include_k)
                        data.get(2).add(k);
                    if (include_psi)
                        data.get(3).add(end / k);
                    if (include_dv)
                        data.get(4).add(end - v0);
                }

                double cumul_dose = Cumul_Dose_Check(k, end, lambda, gamma, delta, fraction_freq, fraction_size, max_dose, indirect, direct, pretreat, hour, data, percent_decr, include_k, include_psi, include_dv);
                if (cumul_dose != -1) // filters LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size)
                {
                    a.setK(k);
                    a.setV0(v0);
                    a.setPSI(v0, k);
                    a.setMinDose(cumul_dose);
                    a.setData(data);
                    a.setlambda(lambda);
                    a.setFractionSize(fraction_size);
                    a.setdelta(delta);
                    a.setalpha(alpha);
                    if (allpts.size() % 100.0 == 0.0 && allpts.size() > 1)
                        System.out.println("Completed: " + allpts.size());
                    allpts.add(a);
                }
            }
        }
    }
}

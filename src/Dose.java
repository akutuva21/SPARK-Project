import java.util.ArrayList;
import java.util.Random;

public class Dose {

    // Return a random number from a list of doubles in an ArrayList (vector)
    public static double getRandom(ArrayList<Double> array, Random r) {
        int rnd = r.nextInt(array.size());
        return array.get(rnd);
    }

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
            p.setcumul_dose(max_dose);
            //p.setmin_dose(max_dose);
            allpts.add(p);
        }
        return Cumulative_Dose_Patients(allpts, direct, indirect, pretreat, hour, r);
    }

    // Sets up the patients and simulates the initial growth period prior to RT
    public static ArrayList<Patient> Cumulative_Dose_Patients(ArrayList<Patient> allpts, boolean direct, boolean indirect, boolean pretreat, ArrayList<Double> hour, Random r)
    {
        double fraction_size; // d
        double fraction_freq = 1;
        double percent_decr = 1 - 0.322;
        double psi = -1; double k; double v0; double lambda = 0; double alpha = 0; double delta = 0;
        int scale = (int) Math.pow(10, 1);
        boolean include_k = false;
        boolean include_psi = false;
        boolean include_dv = false;

        /*ArrayList<Double> frac_size = new ArrayList<>();
        for (double i = 0.1; i <= 30; i += 0.1)
        {
            frac_size.add(i);
        }*/

        int i = 0;
        v0 = 100;
        while (i != allpts.size()) {
            ArrayList<ArrayList<Double>> data = new ArrayList<>();
            for (int n = 0; n < 5; n++)
                data.add(new ArrayList<>()); // time, volume, k_vals, psi_vals, dv_vals
            //fraction_size = getRandom(frac_size, r);
            //fraction_size = (double) Math.round(fraction_size * scale) / scale;
            fraction_size = 2;
            allpts.get(i).setmin_dose(allpts.get(i).getcumul_dose());

            boolean linear = !true;
            boolean logistic = !true;
            boolean lq = !true;
            boolean no_change = true;
            //boolean vol_comp = !true; // diregard
            double abratio = 10;

            /*if (i < allpts.size() / 3)
                linear = true;
            else if (i < 2 * allpts.size() / 3)
                logistic = true;
            else
                lq = true;*/

            //lambda = getRandom(new ArrayList<>(Arrays.asList(.1, .2, .3, .4, .5)), r);
            //delta = getRandom(new ArrayList<>(Arrays.asList(.1, .2)), r);
            if (direct)
            {
                psi = In_Silico.next(r, 2.54/2.93, 2.19/2.93, 2.76/2.93, 1.62/2.93, 2.86/2.93);
                //lambda = Math.abs(r.nextGaussian() * 0.02 + 0.07);
                lambda = 0.07;
                //alpha = 0.09;
                alpha = Math.abs(r.nextGaussian() * 0.02 + 0.09);

                //alpha = r.nextDouble();
                /* if (i < allpts.size() / 8)
                {
                    abratio = 4;
                    delta = abratio;
                }
                else if (i < 2 * allpts.size() / 8)
                {
                    abratio = 6;
                    delta = abratio;
                }
                else if (i < 3 * allpts.size() / 8)
                {
                    abratio = 8;
                    delta = abratio;
                }
                else if (i < 4 * allpts.size() / 8)
                {
                    abratio = 12;
                    delta = abratio;
                }

                else if (i < 5 * allpts.size() / 8)
                {
                    abratio = 16;
                    delta = abratio;
                }
                else if (i < 6 * allpts.size() / 8)
                {
                    abratio = 20;
                    delta = abratio;
                }
                else if (i < 7 * allpts.size() / 8)
                {
                    abratio = 24;
                    delta = abratio;
                }
                else
                {
                    abratio = 28;
                    delta = abratio;
                }*/
            }
            if (indirect)
            {
                psi = In_Silico.next(r, 3.7/4.11, 3.03/4.11, 3.92/4.11, 2.27/4.11, 4.08/4.11);
                lambda = In_Silico.next(r, 0.49/3.72 * 0.6, 0.31/3.72 * 0.6, 0.93/3.72 * 0.6, 0.31/3.72 * 0.6, 1.31/3.72 * 0.6);

                //fraction_size = 5;
                //psi = 0.9 - 0.05 * i; // ranges from 0.9 to 0.6 - 7
                //fraction_size = i + 1; // ranges from 1 to 10 - 10
                //psi = 0.9;

                /*if (fraction_size > 3)
                    continue;

                if (i < allpts.size() / 8)
                    psi = 0.95;
                else if (i < 2 * allpts.size() / 8)
                    psi = 0.90;
                else if (i < 3 * allpts.size() / 8)
                    psi = 0.85;
                else if (i < 4 * allpts.size() / 8)
                    psi = 0.8;
                else if (i < 5 * allpts.size() / 8)
                    psi = 0.75;
                else if (i < 6 * allpts.size() / 8)
                    psi = 0.7;
                else if (i < 7 * allpts.size() / 8)
                    psi = 0.65;
                else
                    psi = 0.6;*/

                if (no_change)
                {
                    lambda = 0.49/3.72 * 0.6; // median lambda
                    delta = In_Silico.next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
                }
                else if (linear)
                {
                    //delta = In_Silico.next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
                    delta = 0.1 * 1.45/4.31;

                    /*if (i < allpts.size() / 3)
                    {
                        delta = 0.1 * 0.95/4.31; // q1
                        alpha = 0;

                    }
                    else if (i < 2 * allpts.size() / 3)
                    {
                        delta = 0.1 * 1.45/4.31; // median
                        alpha = 1;
                    }
                    else
                    {
                        delta = 0.1 * 2.38/4.31; // q3
                        alpha = 2;
                    }*/

                    double ratio = 2.0 / delta; // median delta at 2 Gy
                    lambda = 0.49/3.72 * 0.6; // median lambda
                    delta = fraction_size / ratio;
                }
                else if (logistic)
                {
                    //lambda = 0.1;
                    //psi = r.nextDouble();
                    //double C = 181;
                    /*if (i < allpts.size() / 3)
                    {
                        y = 0.1 * 0.95/4.31; // q1
                        alpha = 0;

                    }
                    else if (i < 2 * allpts.size() / 3)
                    {
                        y = 0.1 * 1.45/4.31; // median
                        alpha = 1;
                    }
                    else
                    {
                        y = 0.1 * 2.38/4.31; // q3
                        alpha = 2;
                    }*/
                    double y = 0.1 * 1.45/4.31; // median delta
                    double x = 2; // dose (x)
                    // C derived from inverse of logistic equation (delta = e^d / (e^d + c) - 1 / (1 + c))
                    double v = -Math.exp(x) * y - y + Math.exp(x) - 1;
                    double sqrt = Math.sqrt(-2 * Math.exp(x) * Math.pow(y, 2) + Math.exp(2 * x) * Math.pow(y, 2) + Math.pow(y, 2) - 2 * Math.exp(2 * x) * y + 2 * y - 2 * Math.exp(x) + Math.exp(2 * x) + 1);
                    double C1 = (v + sqrt) / (2 * y); // c1 = 181.48
                    double C2 = (v - sqrt) / (2 * y); // c2 = 0.041

                    //if (i < allpts.size() / 2)
                        //delta = (Math.exp(fraction_size) / (Math.exp(fraction_size) + C1)) - (1/(1 + C1));
                    //else
                        //delta = (Math.exp(fraction_size) / (Math.exp(fraction_size) + C2)) - (1/(1 + C2));

                    delta = Math.exp(fraction_size) / (Math.exp(fraction_size) + C1) - 1/(1 + C1);
                    lambda = 0.49/3.72 * 0.6; // median lambda
                }
                else if (lq)
                {
                    double ab_ratio = 10;
                    //double a = 0.0143;

                    /*if (i < allpts.size() / 3)
                    {
                        delta = 0.1 * 0.95/4.31; // q1
                        alpha = 0;
                    }
                    else if (i < 2 * allpts.size() / 3)
                    {
                        delta = 0.1 * 1.45/4.31; // median
                        alpha = 1;
                    }
                    else
                    {
                        delta = 0.1 * 2.38/4.31; // q3
                        alpha = 2;
                    }*/

                    delta = 0.1 * 1.45/4.31;
                    double ref_dose = 2;
                    double a = Math.log(1 - delta) / (-ref_dose - Math.pow(ref_dose, 2) / ab_ratio);
                    delta = 1 - Math.exp(-a * fraction_size - (a/ab_ratio) * Math.pow(fraction_size, 2));
                    lambda = 0.49/3.72 * 0.6; // median lambda
                }
                /*else if (vol_comp) // disregard
                {
                    boolean temp;
                    double delta_t = 1.0 / 24.0;

                    while (true) {
                        delta = r.nextDouble();
                        psi = In_Silico.next(r, 3.7/4.11, 3.03/4.11, 3.92/4.11, 2.27/4.11, 4.08/4.11);
                        lambda = In_Silico.next(r, 0.49/3.72 * 0.6, 0.31/3.72 * 0.6, 0.93/3.72 * 0.6, 0.31/3.72 * 0.6, 1.31/3.72 * 0.6);

                        double dose = getRandom(frac_size, r);
                        temp_alpha = r.nextDouble();
                        double start = 100;

                        double gamma = 1 - Math.exp(-temp_alpha * dose - temp_alpha / 10 * Math.pow(dose, 2));
                        ArrayList<Double> direct_vals = new ArrayList<>();
                        ArrayList<Double> indirect_vals = new ArrayList<>();
                        for (int o = 0; o < 2; o++)
                        {
                            temp = o % 2 == 1;
                            double t = 0;
                            double end = start;
                            k = v0 / psi;
                            double numdoses = 0;
                            while (numdoses <= 10)
                            {
                                t += delta_t;
                                double weekend = (int) (t / delta_t) % (7 / delta_t);
                                // if not weekend
                                if (weekend <= 120) for (double h : hour) {
                                    if (((int) (t / delta_t) - h) % (1 / delta_t) == 0) // everyday at h hour
                                    {
                                        if (temp)
                                            end -= gamma * end * (1 - (end / k)); //direct cell kill
                                        else
                                            k *= (1 - delta); //indirect cell kill
                                        numdoses++;
                                        end = k / (1 + ((k / end) - 1) * Math.exp(-lambda * delta_t));
                                    }
                                }
                                else
                                    end = k / (1 + ((k / end) - 1) * Math.exp(-lambda * delta_t));
                                if (temp)
                                    direct_vals.add(end);
                                else
                                    indirect_vals.add(end);
                            }
                        }
                        int q = 0;
                        for (int p = 0; p < direct_vals.size(); p++)
                        {
                            if (In_Silico.nearZero(direct_vals.get(p) - indirect_vals.get(p), 0.05 * direct_vals.get(p)))
                            {
                                q++;
                            }
                        }
                        if (q >= .99 * direct_vals.size())
                            break;
                    }
                }*/
                delta = 0.07;
                psi = 0.7;
            }
            //direct = alpha != 0;
            //indirect = delta != 0;
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

            double cumul_dose = Cumul_Dose_Check(k, end, lambda, gamma, delta, fraction_freq, fraction_size, allpts.get(i).getmin_dose(), indirect, direct, pretreat, hour, data, percent_decr, include_k, include_psi, include_dv);
            if (cumul_dose != -1) // filters LRC (8 weeks of treatment or while number of doses <= maximum dose / fraction size)
            {
                allpts.get(i).set_k(k);
                allpts.get(i).set_v0(v0);
                allpts.get(i).setpsi(v0, k);
                allpts.get(i).setmin_dose(cumul_dose);
                allpts.get(i).setdata(data);
                allpts.get(i).setlambda(lambda);
                allpts.get(i).setfraction_size(fraction_size);
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
        double delta_t = 1.0 / 24.0;
        double start = end;
        double maxtime = 8 * 7;
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
                        end -= gamma * end * (1 - (end / k)); //direct cell kill
                    }
                    if (indirect) {
                        k *= (1 - delta); //indirect cell kill
                    }
                    if (direct || indirect)
                        numdoses++;
                }
            }
            end = k / (1 + ((k / end) - 1) * Math.exp(-lambda * delta_t));
            if (pretreat)
                data.get(0).add(t + fraction_freq); // time
            else
                data.get(0).add(t);
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
        double fraction_freq = 1;
        double percent_decr = 1 - 0.322;
        double k;
        double v0;
        double alpha = 0; double delta = 0;

        int scale = (int) Math.pow(10, 1);
        boolean include_k = false;
        boolean include_psi = false;
        boolean include_dv = false;

        v0 = 100;
        double lambda = 0.07;
        double[] alpha_range = {0.06, 0.14};
        double[] delta_range = {0.01, 0.09};
        double[] psi_range = {0.6, 1};
        double[] incr = new double[2];
        if (direct) incr = new double[]{0.0005, 0.0005};
        if (indirect) incr = new double[]{0.001, 0.001};
        double[] range;
        if (direct) range = alpha_range;
        else range = delta_range;

        for (double x = range[0]; x <= range[1]; x += incr[0]) {
            if (indirect) delta = x;
            if (direct) alpha = x;
            for (double psi = psi_range[0]; psi <= psi_range[1]; psi += incr[1]) {
                Patient a = new Patient();
                ArrayList<ArrayList<Double>> data = new ArrayList<>();
                for (int n = 0; n < 5; n++)
                    data.add(new ArrayList<>()); // time, volume, k_vals, psi_vals, dv_vals
                fraction_size = 2;

                double max_dose = 120.0;
                a.setcumul_dose(max_dose);

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
                    a.set_k(k);
                    a.set_v0(v0);
                    a.setpsi(v0, k);
                    a.setmin_dose(cumul_dose);
                    a.setdata(data);
                    a.setlambda(lambda);
                    a.setfraction_size(fraction_size);
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

/*
if (direct)
    {
    //p.setlambda(Math.abs(r.nextGaussian() * 0.02 + 0.07));
    //p.setalpha(Math.abs(r.nextGaussian() * 0.02 + 0.09));
    //p.setpsi(In_Silico.next(r, 2.54/2.93, 2.19/2.93, 2.76/2.93, 1.62/2.93, 2.86/2.93));
    }
if (indirect)
    {
    //p.setlambda(In_Silico.next(r, 0.49/3.72 * 0.6, 0.31/3.72 * 0.6, 0.93/3.72 * 0.6, 0.31/3.72 * 0.6, 1.31/3.72 * 0.6));
    //p.setpsi(In_Silico.next(r, 3.7/4.11, 3.03/4.11, 3.92/4.11, 2.27/4.11, 4.08/4.11));
    //p.setdelta(In_Silico.next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
    }
*/

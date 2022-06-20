import java.util.ArrayList;
import java.util.Random;

// Unused currently, just used for spawning patients without any basic criteria for selection
public class In_Silico {

    // Function to approximate values near zero given a value f (actual - expected) and an epsilon range for error
    static boolean nearZero(double f, double epsilon) {
        return ((-epsilon < f) && (f < epsilon));
    }

    // Samples values from a boxplot distribution given q1, median, q3, min, and max
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

    // Spawns arbitrary patients given parameters
    public static ArrayList<Patient> PatientSpawner(int numpatients, ArrayList<Double> hour, double lambda, boolean direct, boolean indirect, boolean pretreat) {
        ArrayList<Patient> allpts = new ArrayList<>();
        double alpha = 0; double delta = 0; double psi = -1;

        double fraction_size = 2; // d

        int scale = (int) Math.pow(10, 1);

        double abratio = 10;
        double mu = 1.0;
        double cumul_dose = 68.0; // D
        //int[] hour = new int[]{6};
        double fraction_freq = hour.size();
        int i = 0;

        while (i < numpatients) {
            //double fraction_size = Dose.getRandom(frac_size, r);
            fraction_size = (double) Math.round(fraction_size * scale) / scale;

            if (direct && !indirect)
            {
                //psi = In_Silico.next(r, 2.54/2.93, 2.19/2.93, 2.76/2.93, 1.62/2.93, 2.86/2.93);
                psi = 2.54/2.93; // median psi
                lambda = 0.07; // constant lambda
                alpha = 0.09; // constant alpha
            }
            if (indirect && !direct)
            {
                //psi = In_Silico.next(r, 3.7/4.11, 3.03/4.11, 3.92/4.11, 2.27/4.11, 4.08/4.11);
                psi = 3.7/4.11; // median psi
                lambda = 0.49/3.72 * 0.6; // median lambda
                //delta = In_Silico.next(r, 0.1 * 1.45/4.31, 0.1 * 0.95/4.31, 0.1 * 2.38/4.31, 0.1 * 0.31/4.31, 0.1 * 4.11/4.31);
                delta = 0.1 * 1.45/4.31; // median delta
            }

            //alpha = 0.12;
            //lambda = 0.1;
            //psi = 0.8;

            //lambda = 0.1;
            //double delta = next(r, 0.1 * 1.37 / 4.01, 0.1 * 0.94 / 4.01, 0.1 * 2.24 / 4.01, 0.1 * 0.29 / 4.01, 0.1 * 3.84 / 4.01); // assumed to be gaussian model despite box plot
            //lambda = r.nextDouble() * Math.log(2);
            //do {
            //  psi = next(r, 3.6 / 4.01, 2.97 / 4.01, 3.81 / 4.01, 2.2 / 4.01, 1.0); // assumed to be gaussian despite box plot
            //} while (!(psi <= 1) || !(psi >= 0));

            ArrayList<ArrayList<Double>> data = new ArrayList<>(); // stores various data points for a given patient
            for (int k = 0; k < 5; k++)
                data.add(new ArrayList<>()); // time, volume, k_vals, psi_vals, dv_vals

            double start = 100;
            double k = start / psi;
            //double k = 100;
            //double start = 1;

            alpha = 0;
            delta = 0.1;
            lambda = 0.1;
            psi = 0.8;

            data.get(0).add(0.0);
            data.get(1).add(start);
            data.get(2).add(k);
            data.get(3).add(psi);
            double end = start;

            if (pretreat)
            {
                end = k / (1 + ((k / start) - 1) * Math.exp(-lambda * fraction_freq));

                data.get(0).add(fraction_freq);
                data.get(1).add(end); // simulating only growth from first time point as v(diagnosis)
                data.get(2).add(k);
                data.get(3).add(end / k);
                data.get(4).add(end - start);
            }

            double gamma = 1 - Math.exp(-alpha * fraction_size - (alpha / abratio) * Math.pow(fraction_size, 2));
            boolean psi_check = SimulateForward(k, end, lambda, gamma, delta, fraction_freq, fraction_size, cumul_dose, indirect, direct, pretreat, hour, data);

            if (!psi_check)
            {
                Patient p = new Patient();
                p.setlambda(lambda);
                p.setalpha(alpha);
                p.setdelta(delta);
                p.setPSI(psi);
                p.setData(data);
                p.setmu(mu);
                p.setFractionSize(fraction_size);
                allpts.add(p);
                i++;
            }
        }
        return allpts;
    }

    // Simulates patient volume over time without an LRC cap
    public static boolean SimulateForward(double k, double end, double lambda, double gamma, double delta, double fraction_freq, double fraction_size, double cumul_dose, boolean indirect, boolean direct, boolean pretreat, ArrayList<Double> hour, ArrayList<ArrayList<Double>> data) {
        double t = 0;
        int numdoses = 0;
        double delta_t = 1.0 / 24.0;
        double maxtime = 8 * 7; // 8 weeks of treatment

        while (t < maxtime && numdoses <= cumul_dose / fraction_size) {
            if (direct && indirect && end / k > 1)
                return true;

            t += delta_t;
            int weekend = (int) (t * 24) % (7 * 24);
            double end0 = end;
            if (weekend <= 120) { // if not weekend
                if (Dose.ArrayCheck(t, delta_t, hour)) // everyday at h hour
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
                data.get(0).add(t); // time
            data.get(1).add(end); // volume
            data.get(2).add(k); // k
            data.get(3).add(end / k); // psi
            data.get(4).add(end - end0); // dV
        }
        return false;
    }

    // Used to find a plane to separate patients such that PSI(i) > 1 and PSI(i) <= 1 for all i
    // Time Consuming process since it relies on random numbers without an optimization - though better than Linear SVM
    public static double[] Plane_Finder(ArrayList<Patient> allpts, double bound) {
        double a, b, c, d;
        int check_true = 0, check_false = 0, count = 0;
        Random r = new Random();

        for (Patient p : allpts) {
            if (p.getExceedsOne())
                check_true++;
            else
                check_false++;
        }

        double above1 = 0.0;
        double below1 = 0.0;
        double[] maxparam = new double[] {0.0, 0.0, 0.0, 0.0};
        int above;
        int below;

        while (true) {
            above = 0;
            below = 0;
            //a = r.nextGaussian() * Math.pow(10, -4);
            //b = r.nextGaussian() * Math.pow(10, -4);
            //c = r.nextGaussian() * Math.pow(10, -4);
            //d = r.nextGaussian() * Math.pow(10, -4);
            if (((double) above / check_true) > bound * 0.95 && ((double) below / check_false) > bound * 0.95) {
                a = maxparam[0] * 1.5 * r.nextDouble();
                b = maxparam[1] * 1.5 * r.nextDouble();
                c = maxparam[2] * 1.5 * r.nextDouble();
                d = maxparam[3] * 1.5 * r.nextDouble();
            }
            else
            {
                a = r.nextDouble() * Math.pow(10, -4);
                b = -r.nextDouble() * Math.pow(10, -4);
                c = r.nextDouble() * Math.pow(10, -4);
                d = -r.nextDouble() * Math.pow(10, -4);
                //a = 27.25210074470777; b = -22.285723990476423; c = 183.74005670295253; d = -2.7828695333059703;
            }

            for (Patient p : allpts) {
                if (p.getExceedsOne())
                {
                    if (a * p.getlambda() + b * p.getalpha() + c * p.getdelta() + d > 0)
                        above++; // PSI > 1
                }
                if (!p.getExceedsOne())
                {
                    if (a * p.getlambda() + b * p.getalpha() + c * p.getdelta() + d < 0)
                        below++; // this is the case where PSI <= 1
                }
            }
            if (((double) above / check_true) > bound && ((double) below / check_false) > bound) {
                System.out.println("\n" + count + ", Above = " + above + ", Below = " + below);
                System.out.println((double)above / check_true + ", " + (double)below / check_false);
                break;
            }

            double above2 = (double) above / check_true;
            double below2 = (double) below / check_false;
            if ((above2 >= above1 && below2 >= below1) || (nearZero(above2 - 1, Math.pow(10, -2)) && below2 >= below1) || (above2 >= above1 && nearZero(below2 - 1, Math.pow(10, -2))))
            {
                maxparam = new double[] {a, b, c, d};
                above1 = above2;
                below1 = below2;
                System.out.println(above1 + " " + below1);
                System.out.println(maxparam[0] + "," + maxparam[1] + "," + maxparam[2] + "," + maxparam[3]);
            }

            count++;
            if (count % 10000 == 0) System.out.println(count);
        }
        return new double[]{a, b, c, d, (double)above / check_true, (double)below / check_false, allpts.size()};
    }

    // Spawns arbitrary patients given parameters
    public static ArrayList<Patient> Grid_Search(ArrayList<Double> hour, boolean direct, boolean indirect, boolean pretreat) {
        ArrayList<Patient> allpts = new ArrayList<>();
        double alpha = 0; double delta = 0;
        //double delta = 0;

        double fraction_size = 2; // d

        int scale = (int) Math.pow(10, 1);

        double abratio = 10;
        double mu = 1.0;
        double cumul_dose = 68.0; // D
        double fraction_freq = hour.size();
        double lambda = 0.07;
        double[] alpha_range = {0.06, 0.14};
        double[] delta_range = {0.01, 0.09};
        double[] psi_range = {0.6, 1};
        double[] incr = {0.001, 0.001};
        double[] range;
        if (direct)
        {
            range = alpha_range;
        }
        else
        {
            range = delta_range;
        }

        for (delta = range[0]; delta <= range[1]; delta += incr[0])
            for (double psi = psi_range[0]; psi <= psi_range[1]; psi += 0.01) {
                //double fraction_size = Dose.getRandom(frac_size, r);
                fraction_size = (double) Math.round(fraction_size * scale) / scale;

                ArrayList<ArrayList<Double>> data = new ArrayList<>(); // stores various data points for a given patient
                for (int k = 0; k < 5; k++)
                    data.add(new ArrayList<>()); // time, volume, k_vals, psi_vals, dv_vals

                double start = 100;
                double k = start / psi;

                data.get(0).add(0.0);
                data.get(1).add(start);
                data.get(2).add(k);
                data.get(3).add(psi);
                double end = start;

                if (pretreat)
                {
                    end = k / (1 + ((k / start) - 1) * Math.exp(-lambda * fraction_freq));

                    data.get(0).add(fraction_freq);
                    data.get(1).add(end); // simulating only growth from first time point as v(diagnosis)
                    data.get(2).add(k);
                    data.get(3).add(end / k);
                    data.get(4).add(end - start);
                }

                double gamma = 1 - Math.exp(-alpha * fraction_size - (alpha / abratio) * Math.pow(fraction_size, 2));
                boolean psi_check = In_Silico.SimulateForward(k, end, lambda, gamma, delta, fraction_freq, fraction_size, cumul_dose, indirect, direct, pretreat, hour, data);

                if (!psi_check)
                {
                    Patient p = new Patient();
                    p.setlambda(lambda);
                    p.setalpha(alpha);
                    p.setdelta(delta);
                    p.setPSI(psi);
                    p.setData(data);
                    p.setmu(mu);
                    p.setFractionSize(fraction_size);
                    allpts.add(p);
                }
            }
        return allpts;
    }
}
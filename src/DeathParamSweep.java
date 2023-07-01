public class DeathParamSweep {
    public static void main(String[] args) throws Exception{
        Boolean lrc_filter, dose_filter, time_filter, logistic_start, direct, indirect;
        double[] alpha_range, delta_range, lambda_range, frac_range, psi_range;
        double alpha_incr, delta_incr, lambda_incr, frac_incr, psi_incr;
        String values_name, volume_name, k_name;

        // Figure 3

        // Panel A: DVR Sweep for Varying Alpha

        lrc_filter = false; // not checking for LRC
        dose_filter = false; // not filtering by dose constraints
        time_filter = false; // not filtering by time constraints
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0, 0.2}; // alpha death rate range, can be modified
        delta_range = new double[]{0};
        lambda_range = new double[]{0.1}; // growth rate set constant, can be modified
        frac_range = new double[]{2}; // fraction size set constant, can be modified
        psi_range = new double[]{0.9}; // PSI set constant, can be modified
        alpha_incr = 0.02; // increment of alpha, can be modified
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        volume_name = "l=0.1,psi=0.9,a";
        k_name = "";

        ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_range, 
        delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, lambda_incr, psi_incr, 
        frac_incr, values_name, volume_name, k_name);

        // Panel B: CCR Sweep for Varying Delta

        lrc_filter = false; // not checking for LRC
        dose_filter = false; // not filtering by dose constraints
        time_filter = false; // not filtering by time constraints
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        delta_range = new double[]{0, 0.2}; // delta death rate range, can be modified
        lambda_range = new double[]{0.1}; // growth rate set constant, can be modified
        frac_range = new double[]{2}; // fraction size set constant, can be modified
        psi_range = new double[]{0.9}; // PSI set constant, can be modified
        alpha_incr = 0;
        delta_incr = 0.02; // increment of delta, can be modified
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        volume_name = "l=0.1,psi=0.9,d";
        k_name = "";

        ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_range, 
        delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, lambda_incr, psi_incr, 
        frac_incr, values_name, volume_name, k_name); 
    }
}

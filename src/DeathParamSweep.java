public class DeathParamSweep {
    public static void main(String[] args) throws Exception{
        Boolean lrc_filter, dose_filter, time_filter, logistic_start, direct, indirect;
        double[] alpha_range, delta_range, lambda_range, frac_range, psi_range;
        double alpha_incr, delta_incr, lambda_incr, frac_incr, psi_incr;
        String values_name, volume_name, k_name;

        // Figure 2

        // Left Panel

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0, 0.2};
        delta_range = new double[]{0.1};
        lambda_range = new double[]{0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.9};
        alpha_incr = 0.02;
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

        // Right Panel

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        delta_range = new double[]{0, 0.2};
        lambda_range = new double[]{0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.9};
        alpha_incr = 0;
        delta_incr = 0.02;
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

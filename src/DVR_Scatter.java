public class DVR_Scatter {
    public static void main(String[] args) throws Exception {
        Boolean lrc_filter, dose_filter, time_filter, logistic_start, direct, indirect;
        double[] alpha_range, delta_range, lambda_range, frac_range, psi_range;
        double alpha_incr, delta_incr, lambda_incr, frac_incr, psi_incr;
        String values_name, volume_name, k_name;

        // Figure 4

        // Left Panel: Generate heatmap of alpha and psi over ranges dictated in 

        lrc_filter = true;
        dose_filter = true;
        time_filter = true;
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0.06, 0.14}; // Modify alpha_range here if needed
        delta_range = new double[]{0};
        lambda_range = new double[]{0.07};
        frac_range = new double[]{2};
        psi_range = new double[]{0.6, 1};
        alpha_incr = 0.0005;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0.0005;
        frac_incr = 0;
        values_name = "Alpha_PSI_dose";
        volume_name = "";
        k_name = "";

        ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_range, 
        delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, lambda_incr, psi_incr, 
        frac_incr, values_name, volume_name, k_name);

        // Right Panel: Plot examples from the Left Panel to sample how volume behavior

        lrc_filter = true;
        dose_filter = true;
        time_filter = false;
        direct = true; indirect = false;
        logistic_start = false;
        delta_range = new double[]{0};
        frac_range = new double[]{2};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        k_name = "";
        lambda_range = new double[]{0.07};

        // top left, top right, bottom right, bottom left
        double[][] alpha_ranges = { {0.08}, {0.12}, {0.12}, {0.08} };
        double[][] psi_ranges_direct = { {0.9}, {0.9}, {0.7}, {0.7} };
        String[] volume_names_direct = {"Volume_Direct_topleft", "Volume_Direct_topright", "Volume_Direct_bottomright", "Volume_Direct_bottomleft"}; 

        for (int i = 0; i < 4; i++)
        {
            ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_ranges[i], 
            delta_range, lambda_range, psi_ranges_direct[i], frac_range, alpha_incr, delta_incr, lambda_incr, 
            psi_incr, frac_incr, values_name, volume_names_direct[i], k_name);
        }
    }
}

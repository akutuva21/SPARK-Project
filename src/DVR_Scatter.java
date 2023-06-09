public class DVR_Scatter {
    public static void main(String[] args) throws Exception {
        Boolean lrc_filter, dose_filter, time_filter, logistic_start, direct, indirect;
        double[] alpha_range, delta_range, lambda_range, frac_range, psi_range;
        double alpha_incr, delta_incr, lambda_incr, frac_incr, psi_incr;
        String values_name, volume_name, k_name;

        // Figure 4

        // Panel A: Scatter plot of alpha and PSI over indicated ranges

        lrc_filter = true; // checking for LRC, set to false if not checking for LRC
        dose_filter = true; // filtering by dose constraints, set to false if not filtering by dose
        time_filter = true; // filtering by time constraints, set to false if not filtering by time
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0.06, 0.14}; // death rate range, can be modified
        delta_range = new double[]{0};
        lambda_range = new double[]{0.07}; // growth rate set constant, can be modified
        frac_range = new double[]{2}; // fraction size set constant, can be modified
        psi_range = new double[]{0.6, 1}; // PSI range, can be modified
        alpha_incr = 0.0005; // increment of alpha, can be modified
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0.0005; // increment of PSI, can be modified
        frac_incr = 0;
        values_name = "Alpha_PSI_dose";
        volume_name = "";
        k_name = "";

        ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_range, 
        delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, lambda_incr, psi_incr, 
        frac_incr, values_name, volume_name, k_name);

        // Panel B: Example plots from Panel A to sample volume behavior

        lrc_filter = true; // checking for LRC, set to false if not checking for LRC
        dose_filter = true; // filtering by dose constraints, set to false if not filtering by dose
        time_filter = false; // filtering by time constraints
        direct = true; indirect = false;
        logistic_start = false;
        delta_range = new double[]{0};
        frac_range = new double[]{2}; // fraction size set constant, can be modified
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        k_name = "";
        lambda_range = new double[]{0.07};

        // Indicates the alpha/PSI coordinates of where to sample the volume data
        // Top left, top right, bottom right, bottom left ordering
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

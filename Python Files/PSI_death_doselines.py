## Figures 4/5: Death PSI Cumulative Graphs, Indirect -> Delta, Direct -> Alpha

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from scipy.optimize import curve_fit
from matplotlib import style, cm, colors
import matplotlib.gridspec as gridspec
import csv, os

style.use('default')
wr = (1, 1, 2.5, 2, 2)
grid_hspace = 0.6
fig = plt.figure(figsize=(22, (2 + grid_hspace/2) * 5))
outer_grid = gridspec.GridSpec(2, len(wr), width_ratios=wr, hspace=grid_hspace)

plot_width = 2
font = 20
label_font = 30
axis_thickness = 3

def time_to_dose(t):
    h = 6
    delta_t = 1/24
    counter = 0
    dose = 2
    dose_array = np.zeros(len(t))

    for i in range(0, len(t)):
        time = (int) ((t[i] / delta_t)) % (1 / delta_t)
        weekend = (int) (t[i] / delta_t) % (7 / delta_t)
        if time == h and weekend <= 120:
            counter += 1
        dose_array[i] = dose * counter
    return dose_array

for i in range(0, 2):
    if i == 0:
        direct = True
        indirect = False
    if i == 1:
        direct = False
        indirect = True

    # Create a nested gridspec for the middle two plots
    middle_grid = gridspec.GridSpecFromSubplotSpec(2, 2, subplot_spec=outer_grid[i, 0:2], wspace=0.15, hspace=0.25)

    ax = plt.subplot(outer_grid[i, 2])
    ax.text(-0.35, 1.27, 'B' if direct else 'F', transform=ax.transAxes,
            color='black', fontweight='bold', fontsize=label_font, va='top')

    if direct:
        s = 'Alpha_PSI_dose.csv'
        t = pd.read_csv(s)

        s1 = 'Volume_Direct_bottomleft'
        s2 = 'Volume_Direct_bottomright'
        s3 = 'Volume_Direct_topleft'
        s4 = 'Volume_Direct_topright'

        files = [s1, s2, s3, s4]
        for f in files:
            # Read the CSV file
            with open(f + '.csv', 'r') as file:
                reader = csv.reader(file)
                data = list(reader)

            # Transpose the data
            transposed_data = list(zip(*data))

            # Write the transposed data to a new CSV file
            with open(f + '_T.csv', 'w', newline='') as file:
                writer = csv.writer(file)
                writer.writerows(transposed_data)

        bottomleft = pd.read_csv(str(s1 + '_T.csv'))
        bottomright = pd.read_csv(str(s2 + '_T.csv'))
        topleft = pd.read_csv(str(s3 + '_T.csv'))
        topright = pd.read_csv(str(s4 + '_T.csv'))

        ax.set_xlabel(r'$\alpha$' + ' (' +
                    r'Gy$\mathregular{^{-1}}$' + ')', fontsize=font, labelpad=10)
        ax.set_xlim((0.06, 0.14))

    if indirect:
        s = 'Delta_PSI_dose.csv'
        t = pd.read_csv(s)

        s1 = 'Volume_Indirect_bottomleft'
        s2 = 'Volume_Indirect_bottomright'
        s3 = 'Volume_Indirect_topleft'
        s4 = 'Volume_Indirect_topright'
        s5 = 'k_Indirect_bottomleft'
        s6 = 'k_Indirect_bottomright'
        s7 = 'k_Indirect_topleft'
        s8 = 'k_Indirect_topright'

        files = [s1, s2, s3, s4, s5, s6, s7, s8]
        for f in files:
            # Read the CSV file
            with open(f + '.csv', 'r') as file:
                reader = csv.reader(file)
                data = list(reader)

            # Transpose the data
            transposed_data = list(zip(*data))

            # Write the transposed data to a new CSV file
            with open(f + '_T.csv', 'w', newline='') as file:
                writer = csv.writer(file)
                writer.writerows(transposed_data)

        bottomleft = pd.read_csv(str(s1 + '_T.csv'))
        bottomright = pd.read_csv(str(s2 + '_T.csv'))
        topleft = pd.read_csv(str(s3 + '_T.csv'))
        topright = pd.read_csv(str(s4 + '_T.csv'))
        k_bottomleft = pd.read_csv(str(s5 + '_T.csv'))
        k_bottomright = pd.read_csv(str(s6 + '_T.csv'))
        k_topleft = pd.read_csv(str(s7 + '_T.csv'))
        k_topright = pd.read_csv(str(s8 + '_T.csv'))

        ax.set_xlabel(r'$\delta$', fontsize=font, labelpad=10)
        ax.set_xlim((0.01, 0.09))

    alpha = t.iloc[:, 1]
    delta = t.iloc[:, 2]
    psi = t.iloc[:, 3]
    cumul = t.iloc[:, 4]
    frac = t.iloc[:, 5]
    num = cumul / frac

    ax.set_ylabel('PSI', fontsize=20, labelpad=10)
    ax.tick_params(axis='both', which='major', labelsize=20)
    ax.tick_params(axis='both', which='both', pad=10)

    ax.set_ylim((0.6, 1))
    xmin, xmax = ax.get_xlim()
    ymin, ymax = ax.get_ylim()

    ax.axvspan(xmin, xmax, facecolor='white', alpha=0.15)
    ax.set_xticks(np.linspace(xmin, xmax, 3))
    ax.set_yticks(np.linspace(ymin, ymax, 3))

    cmap = cm.get_cmap('viridis_r')
    c = cumul
    norm = colors.Normalize(c.min(), c.max())

    if direct:
        x = alpha
        y = psi
        manual_locations = [
            (0.112, 0.70), (0.105, 0.78), (0.0985, 0.85), (0.095, 0.88)]
        levels = 12
        ax.scatter(x, y, c=c, alpha=1, cmap=cmap, norm=norm, marker='s', s=10)

    if indirect:
        x = delta
        y = psi
        manual_locations = [
            (0.08, 0.86), (0.065, 0.82), (0.05, 0.785), (0.04, 0.762)]
        levels = 8
        ax.tricontourf(x, y, c, alpha=1, levels=levels, cmap=cmap, norm=norm)
        
    sc = ax.tricontour(x, y, c, colors='k',
        levels=levels, linewidths=1, alpha=1)
    ax.clabel(sc, sc.levels[0:5], fontsize=12, manual=manual_locations, 
              colors='k', inline_spacing=30, fmt='%1.0f Fx')

    x_ticks = ax.get_xticks()
    y_ticks = ax.get_yticks()

    for axis in ['bottom', 'left']:
        ax.spines[axis].set_linewidth(axis_thickness)

    for axis in ['top', 'right']:
        ax.spines[axis].set_linewidth(0)

    ax.tick_params(width=axis_thickness)
    # ax.set_box_aspect(1)

    sm = plt.cm.ScalarMappable(norm=norm, cmap=cmap)
    cb = plt.colorbar(sm, aspect=30, ax=ax)
    cb.ax.set_ylabel('Cumulative Dose (Gy)', labelpad=5, fontsize=font)
    cb.ax.tick_params(axis='both', which='major', labelsize=font)

    if direct:
        x2 = [0.08, 0.12, 0.08, 0.12]
        y = [0.9, 0.9, 0.7, 0.7]
    if indirect:
        x2 = [0.03, 0.07, 0.03, 0.07]
        y = [0.9, 0.9, 0.7, 0.7]

    labels = ["\u2666", "\u2665", "\u2663", "\u2660"]
    for a in range(len(labels)):
        y2 = y[a]
        text = ax.text(x2[a], y2, labels[a], color='black',
                    fontweight='bold', fontsize=label_font)
        
    ##############################################################################

    threshold = 0.322

    read_files = [topleft, topright, bottomleft, bottomright]
    if indirect:
        k_files = [k_topleft, k_topright, k_bottomleft, k_bottomright]

    ax = fig.add_subplot(outer_grid[i, 0:2])
    ax.text(-0.2, 1.27, 'A' if direct else 'E', transform=ax.transAxes,
            color='black', fontweight='bold', fontsize=label_font, va='top')
    ax.set_xlabel('Cumulative Dose (Gy)', fontsize=font, labelpad=40)
    ax.set_ylabel('% Initial Tumor Volume', fontsize=font, labelpad=45)
    ax.set_xticks([])
    ax.set_yticks([])
    ax.patch.set_alpha(0.01)
    for key, spine in ax.spines.items():
        spine.set_visible(False)

    ax_list = [fig.add_subplot(middle_grid[0, 0]), fig.add_subplot(middle_grid[0, 1]),
            fig.add_subplot(middle_grid[1, 0]), fig.add_subplot(middle_grid[1, 1])]

    for val in range(len(read_files)):
        ax = ax_list[val]
        t = read_files[val]

        x = time_to_dose(t.iloc[:, 0])
        ax.plot(x, t.iloc[:, 1], linewidth=plot_width, c='g', zorder=2, label='V')
        if indirect:
            k_file = k_files[val]
            ax.plot(x, k_file.iloc[:, 1], linewidth=plot_width,
                    c='darkorange', zorder=2, label='K')

        if direct:
            ax.set_xlim((0, 80))
            ax.set_ylim((0, 120))
        if indirect:
            ax.set_xlim((0, 80))
            ax.set_ylim((0, 145))

        ymin, ymax = ax.get_ylim()
        xmin, xmax = ax.get_xlim()

        xrange = np.linspace(xmin, xmax, 3)
        yrange = np.linspace(0, 100, 3)
        ax.set_xticks(xrange)
        ax.set_yticks(yrange)
        ax.tick_params(axis='both', which='major', width=axis_thickness)
        
        if (val == 2 or val == 3):
            ax.tick_params(axis='both', which='major', labelsize=font, pad=5)
            ax.set_xticklabels(int(x) for x in xrange)
        else:
            ax.tick_params(labelbottom=False)
        
        if (val == 0 or val == 2):
            ax.tick_params(axis='both', which='major', labelsize=font, pad=5)
            ax.set_yticklabels(int(x) for x in yrange)
        else:
            ax.tick_params(labelleft=False)

        ax.vlines(max(x), 0, ymax, linestyles='dotted',
                color='red', linewidth=plot_width, zorder=3)
        ax.annotate('{0:.0f}'.format(max(x)), xy=(max(x), 0), xytext=(max(x), -15 if direct else -20),
                    fontsize=font * 3/4, ha='center', va='center', color='red')
        ax.hlines((1 - threshold) * 100, 0, xmax, colors='k', linestyles='dotted',
                color='black', linewidth=plot_width, zorder=3)

        for axis in ['bottom', 'left']:
            ax.spines[axis].set_linewidth(axis_thickness)

        for axis in ['right', 'top']:
            ax.spines[axis].set_linewidth(0)

        ax.text(0.10, 0.28, labels[val], transform=ax.transAxes,
                color='black', fontweight='bold', fontsize=label_font, va='top')

        if val == 0 and direct:
            ax.text(max(x) - 5, ymax + 20, 'D$_{min}$', color='red', fontsize=font * 3/4, va='top')
            # ax.annotate('D$_{min}$', xy=(max(x) - 5, ymax), xytext=(max(x) - 5, ymax), color='red', fontsize=font * 3/4)
        if val == 1:
            ax.text(1, (1 - threshold) - (0.1 if direct else 0.2), '-'
                    + r'$\Delta$' + 'V = 32.2%', transform=ax.transAxes, ha='right',
                    color='black', fontweight='bold', fontsize=15, va='bottom')
        if direct:
            ax.text(1, 0.35,
                'PSI' + ' = ' + '{0:.2f}'.format(y[val]),
                transform=ax.transAxes, color='black',
                fontweight='bold', fontsize=15, va='top', ha='right',
                bbox=dict(facecolor='white', edgecolor='none', pad=2.5))
            ax.text(1, 0.2,
                    r'$\alpha$' + ' = ' + '{0:.2f}'.format(x2[val]),
                    transform=ax.transAxes, color='black',
                    fontweight='bold', fontsize=15, va='top',ha='right',
                    bbox=dict(facecolor='white', edgecolor='none', pad=2.5))
        if indirect:
            ax.text(1, 0.975, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),
                        transform=ax.transAxes, color='black', fontweight='bold', fontsize=15, 
                        va='top', ha='right', bbox=dict(facecolor='white', edgecolor='none', pad=2.5))
            ax.text(1, 0.825, r'$\delta$' + ' = ' + '{0:.2f}'.format(x2[val]), 
                    transform=ax.transAxes, color='black', fontweight='bold', fontsize=15, 
                    va='top', ha='right', bbox=dict(facecolor='white', edgecolor='none', pad=2.5))
            if val == 3:
                ax.legend(loc="lower right", fontsize=12)
                ax.xaxis.get_major_ticks()[1].label1.set_visible(False)

    ##############################################################################################################
    point_size = 3
    plot_width_2 = 3

    ax = fig.add_subplot(outer_grid[i, -2])
    ax.text(-0.35, 1.27, 'C' if direct else 'G', transform=ax.transAxes,
            color='black', fontweight='bold', fontsize=label_font, va='top')
    cmap = plt.cm.get_cmap('Greens')
    col = [cmap(0.2), cmap(0.5), cmap(0.8)]

    psi_elem = [0.7, 0.8, 0.9]
    for j in range(0, len(psi_elem)):
        p = psi_elem[j]
        mask = psi == p
        x = alpha[mask] if direct else delta[mask]
        y = cumul[mask]
        ax.scatter(x, y, color=col[j], s=point_size, linewidths=0, label=p)

        # fit a negative exponential curve to sc
        try:
            popt, pcov = curve_fit(lambda t, a, b, c: a * np.exp(b * t) + c, x, y, bounds=([0, -200, 0], [1500, 0, 200]), maxfev=100000)
            a = popt[0]
            b = popt[1]
            c = popt[2]
            y_fitted = a * np.exp(b * x) + c
            # print(f"{'direct' if direct else 'indirect'}, PSI = {p}: {a:.2f}, {b:.2f}, {c:.2f}")
            ax.plot(x, y_fitted, color=col[j], linewidth=plot_width_2, zorder=3)
        except:
            pass

    ax.set_ylim(0, 80)

    # ax.legend(loc="upper right", title="PSI", fontsize=font, title_fontsize=font, markerscale=3)
    ax.legend(title="PSI", fontsize=12, title_fontsize=font * 3/4, markerscale=3, 
              loc="upper right")

    for axis in ['bottom', 'left']:
            ax.spines[axis].set_linewidth(axis_thickness)
    for axis in ['right', 'top']:
        ax.spines[axis].set_linewidth(0)
    ax.tick_params(axis='both', which='major', labelsize=20)
    ax.tick_params(axis='both', which='both', pad=10)
    ax.set_xlabel(r'$\alpha$' + ' (' +
                    r'Gy$\mathregular{^{-1}}$' + ')' if direct else r'$\delta$', fontsize=font, labelpad=10)
    ax.set_ylabel('Cumulative Dose (Gy)', fontsize=font, labelpad=10)
    
    ax.grid(True, zorder=0, linestyle='dashed', linewidth=0.5, which='major')
    num_gridlines = 4
    ax.xaxis.set_major_locator(plt.MaxNLocator(num_gridlines + 1))
    ax.yaxis.set_major_locator(plt.MaxNLocator(num_gridlines + 1))
    ax.set_xticks(x_ticks)
    
    # ax.set_box_aspect(1)
    # ax.set_box_aspect(1)

    ##############################################################################################################

    ax = fig.add_subplot(outer_grid[i, -1])
    ax.text(-0.35, 1.27, 'D' if direct else 'H', transform=ax.transAxes,
            color='black', fontweight='bold', fontsize=label_font, va='top')
    cmap = plt.cm.get_cmap('Blues')
    col = [cmap(0.3), cmap(0.6), cmap(0.9)]

    if direct:
        death_elem = [0.08, 0.10, 0.12]
    if indirect:
        death_elem = [0.03, 0.05, 0.07]
    for i in range(len(death_elem)):
        mask = (alpha == death_elem[i]) if direct else (delta == death_elem[i])
        x = psi[mask]
        y = cumul[mask]
        ax.scatter(x, y, color=col[i], s=point_size, linewidths=0, label=death_elem[i])

        # fit a negative exponential curve to sc
        try:
            if direct:
                popt, pcov = curve_fit(lambda t, a, b, c: a * np.exp(b * t) + c, x, y, bounds=([0, 0, 0], [20, 20, 20]), maxfev=10000000)
            if indirect:
                popt, pcov = curve_fit(lambda t, a, b, c: a * np.exp(b * t) + c, x, y, bounds=([80, -3, 0], [200, -1, 16]), maxfev=10000000)
            a = popt[0]
            b = popt[1]
            c = popt[2]
            # print(f"{'direct' if direct else 'indirect'}, {'alpha' if direct else 'delta'} = {death_elem[i]}: {a:.2f}, {b:.2f}, {c:.2f}")
            y_fitted = a * np.exp(b * x) + c
            ax.plot(x, y_fitted, color=col[i], linewidth=plot_width_2, zorder=3)
        except:
            pass

    ax.set_xticks(y_ticks)
    ax.set_ylim(0, 80)

    ax.legend(title=r'$\alpha$' + ' (' +
                    r'Gy$\mathregular{^{-1}}$' + ')' if direct else r'$\delta$', fontsize=12, title_fontsize=font * 3/4, markerscale=3, 
              loc="upper left" if direct else "upper right")

    for axis in ['bottom', 'left']:
        ax.spines[axis].set_linewidth(axis_thickness)
    for axis in ['right', 'top']:
        ax.spines[axis].set_linewidth(0)
    ax.tick_params(axis='both', which='major', labelsize=20)
    ax.tick_params(axis='both', which='both', pad=10)
    ax.set_xlabel("PSI", fontsize=font, labelpad=10)
    ax.set_ylabel('Cumulative Dose (Gy)', fontsize=font, labelpad=10)
    
    ax.grid(True, zorder=0, linestyle='dashed', linewidth=0.5, which='major')
    num_gridlines = 4
    ax.xaxis.set_major_locator(plt.MaxNLocator(num_gridlines + 1))
    ax.yaxis.set_major_locator(plt.MaxNLocator(num_gridlines + 1))
    ax.set_xticks(np.linspace(0.6, 1, 3))

    ##############################################################################################################

plt.subplots_adjust(left=0.07, bottom=0.133, right=0.98, top=0.87, wspace=0.65)

current_dir = os.getcwd()
parent_dir = os.path.abspath(os.path.join(current_dir, os.pardir))
folder_path = os.path.join(parent_dir, 'Figures')
os.makedirs(folder_path, exist_ok=True)
plt.savefig(os.path.join(folder_path, "Figure_5.png"), bbox_inches='tight', dpi=300)

# plt.show()
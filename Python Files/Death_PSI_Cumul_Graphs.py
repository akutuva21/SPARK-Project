## Figures 4/5: Death PSI Cumulative Graphs, Indirect -> Delta, Direct -> Alpha

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style, cm, colors
import matplotlib.gridspec as gridspec
import csv, sys, os

style.use('default')
fig = plt.figure(figsize=(6.8*2, 5))
outer_grid = gridspec.GridSpec(2, 4, width_ratios=(2, 0.25, 1, 1), hspace=0.2)

direct = sys.argv[1] == 'direct'
indirect = sys.argv[1] == 'indirect'
plot_width = 3
font = 20
label_font = 30
axis_thickness = 3

ax = plt.subplot(outer_grid[:, 0])
ax.text(-0.275, 1.15, 'A', transform=ax.transAxes,
        color='black', fontweight='bold', fontsize=label_font, va='top')

directory = ''

s = directory + 'Alpha_PSI_dose.csv'
t = pd.read_csv(s)
s2 = directory + 'Delta_PSI_dose.csv'
t2 = pd.read_csv(s2)

if direct:
    s1 = directory + 'Volume_Direct_bottomleft'
    s2 = directory + 'Volume_Direct_bottomright'
    s3 = directory + 'Volume_Direct_topleft'
    s4 = directory + 'Volume_Direct_topright'

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

if indirect:
    s1 = directory + 'Volume_Indirect_bottomleft'
    s2 = directory + 'Volume_Indirect_bottomright'
    s3 = directory + 'Volume_Indirect_topleft'
    s4 = directory + 'Volume_Indirect_topright'
    s5 = directory + 'k_Indirect_bottomleft'
    s6 = directory + 'k_Indirect_bottomright'
    s7 = directory + 'k_Indirect_topleft'
    s8 = directory + 'k_Indirect_topright'

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

cumul_direct = t.iloc[:, 4]
cumul_indirect = t2.iloc[:, 4]

if direct:
    alpha = t.iloc[:, 1]
    psi_direct = t.iloc[:, 3]
    frac_size_direct = t.iloc[:, 5]
    num = cumul_direct / frac_size_direct

if indirect:
    psi_indirect = t2.iloc[:, 3]
    frac_size_indirect = t2.iloc[:, 5]
    num = cumul_indirect / frac_size_indirect
    delta = t2.iloc[:, 2]

if direct:
    ax.set_xlabel(r'$\alpha$' + ' (' +
                  r'Gy$\mathregular{^{-1}}$' + ')', fontsize=font, labelpad=10)
    ax.set_xlim((0.06, 0.14))

if indirect:
    ax.set_xlabel(r'$\delta$', fontsize=font, labelpad=10)
    ax.set_xlim((0.01, 0.09))

ax.set_ylabel('PSI', fontsize=20, labelpad=10)
ax.tick_params(axis='both', which='major', labelsize=20)
ax.tick_params(axis='both', which='both', pad=10)

ax.set_ylim((0.6, 1))
xmin, xmax = ax.get_xlim()
ymin, ymax = ax.get_ylim()

ax.axvspan(xmin, xmax, facecolor='white', alpha=0.15)
ax.set_xticks(np.linspace(xmin, xmax, 3))
ax.set_yticks(np.linspace(ymin, ymax, 3))

if direct:
    x = alpha
    y = psi_direct
    cumul = cumul_direct
if indirect:
    x = delta
    y = psi_indirect
    cumul = cumul_indirect

cmap = cm.get_cmap('viridis_r')
norm = colors.Normalize(min(cumul_direct.min(), cumul_indirect.min()), max(
    cumul_direct.max(), cumul_indirect.max()))

if indirect:
    manual_locations = [
        (0.08, 0.86), (0.065, 0.82), (0.05, 0.785), (0.04, 0.762)]

    cond = x < 0.3
    ax.tricontourf(x[cond], y[cond], cumul[cond],
                   alpha=1, cmap=cmap, norm=norm)
    ax.scatter(x[~cond], y[~cond], c=cumul[~cond], alpha=1,
               cmap=cmap, norm=norm, marker='s', s=10)

    sc = ax.tricontour(x, y, num, colors='k', linewidths=1, alpha=1)
    ax.clabel(sc, sc.levels[0:5], fontsize=12, colors='k',
              manual=manual_locations, inline_spacing=7)

if direct:
    ax.scatter(x, y, c=cumul, marker='s', s=10, cmap=cmap, norm=norm)
    # ax.tricontourf(x,y,cumul, cmap = cmap, norm = norm)
    levels = 12
    manual_locations = [
        (0.112, 0.70), (0.105, 0.78), (0.0985, 0.85), (0.095, 0.88)]
    sc = ax.tricontour(x, y, num, colors='k',
                       levels=levels, linewidths=1, alpha=1)
    ax.clabel(sc, sc.levels[0:5], fontsize=12,
              manual=manual_locations, colors='k')

for axis in ['bottom', 'left']:
    ax.spines[axis].set_linewidth(axis_thickness)

for axis in ['top', 'right']:
    ax.spines[axis].set_linewidth(0)
ax.tick_params(width=axis_thickness)

sm = plt.cm.ScalarMappable(norm=norm, cmap=cmap)
cb = plt.colorbar(sm, aspect=30, ax=ax)
cb.ax.set_ylabel('Cumulative Dose (Gy)', labelpad=15, fontsize=font)
cb.ax.tick_params(axis='both', which='major', labelsize=font)

if direct:
    x2 = [0.08, 0.12, 0.08, 0.12]
    y = [0.9, 0.9, 0.7, 0.7]
if indirect:
    x2 = [0.03, 0.07, 0.03, 0.07]
    y = [0.9, 0.9, 0.7, 0.7]

labels = [r'$\blacksquare$', r'$\bigstar$', r'$\clubsuit$', r'$\spadesuit$']
for a in range(len(labels)):
    y2 = y[a]
    if a == 0:
        y2 = 0.905
    text = ax.text(x2[a], y2, labels[a], color='black',
                   fontweight='bold', fontsize=label_font if a > 0 else 20)
    
plt.subplots_adjust(bottom=0.15)

##############################################################################

threshold = 0.322

read_files = [topleft, topright, bottomleft, bottomright]
if indirect:
    k_files = [k_topleft, k_topright, k_bottomleft, k_bottomright]

ax_list = [plt.subplot(outer_grid[0, 2]), plt.subplot(outer_grid[0, 3]),
           plt.subplot(outer_grid[1, 2]), plt.subplot(outer_grid[1, 3])]

for val in range(len(read_files)):
    ax = ax_list[val]
    weeks = 5 if direct else 7
    scaling = 12 if direct else 11.375

    t = read_files[val]
    if indirect:
        k_file = k_files[val]

    x = scaling * (t.iloc[:, 0]) / 7
    c = 'g'
    ax.plot(x, t.iloc[:, 1], linewidth=plot_width, c=c, zorder=2, label='V')
    if indirect:
        ax.plot(x, k_file.iloc[:, 1], linewidth=plot_width,
                c='darkorange', zorder=2, label='K')

    if direct:
        ax.set_xlim((0, weeks * scaling))
        ax.set_ylim((0, 100))
    if indirect:
        ax.set_xlim(0, weeks * scaling)
        ax.set_ylim((0, 145))

    ymin, ymax = ax.get_ylim()
    xmin, xmax = ax.get_xlim()

    xrange = [x for x in np.arange(xmin, xmax + 1)]
    yrange = [0, 50, 100]
    new_ticks = [min(xrange), (min(xrange) + max(xrange)) / 2, max(xrange)]
    ax.set_xticks(new_ticks)
    ax.set_yticks(yrange)
    ax.tick_params(axis='both', which='major', width=axis_thickness)
    if (val == 2 or val == 3):
        ax.tick_params(axis='both', which='major', labelsize=font, pad=5)
        ax.set_xticklabels(int(x) for x in new_ticks)
    else:
        ax.tick_params(labelbottom=False)
    if (val == 0 or val == 2):
        ax.tick_params(axis='both', which='major', labelsize=font, pad=5)
        ax.set_yticklabels(int(x) for x in yrange)
    else:
        ax.tick_params(labelleft=False)

    ax.vlines(max(x), 0, ymax, linestyles='dotted',
              color='black', linewidth=plot_width, zorder=3)
    ax.hlines((1 - threshold) * 100, 0, xmax, colors='k', linestyles='dotted',
              color='black', linewidth=plot_width, zorder=3)

    for axis in ['bottom', 'left']:
        ax.spines[axis].set_linewidth(axis_thickness)

    for axis in ['right', 'top']:
        ax.spines[axis].set_linewidth(0)

    ax.text(0.10, 0.25, labels[val], transform=ax.transAxes,
            color='black', fontweight='bold', fontsize=label_font
            if val > 0 else 20, va='top')

    if val == 3 and indirect:
        ax.legend(loc="lower right", fontsize=12)
    if val == 1:
        ax.text(0.1, 1.1, 'Required Dose', transform=ax.transAxes, wrap=True,
                color='black', fontweight='bold', fontsize=10, va='top')
        ax.text(0.475, 0.85 if direct else 0.625, '-'
                + r'$\Delta$' + 'V = 32.2%', transform=ax.transAxes,
                color='black', fontweight='bold', fontsize=15, va='top')
    if direct:
        ax.text(0.5 if val != 0 else 0.3, 0.35,
                'PSI' + ' = ' + '{0:.2f}'.format(y[val]),
                transform=ax.transAxes, color='black',
                fontweight='bold', fontsize=15, va='top')
        ax.text(0.6 if val != 0 else 0.4, 0.2 if val != 0 else 0.2,
                r'$\alpha$' + ' = ' + '{0:.2f}'.format(x2[val]),
                transform=ax.transAxes, color='black',
                fontweight='bold', fontsize=15, va='top')
    if indirect:
        if val == 0:
            ax.text(0.2, 0.825, r'$\delta$' + ' = ' +
                    '{0:.2f}'.format(x2[val]), transform=ax.transAxes,
                    color='black', fontweight='bold', fontsize=15, va='top')
            ax.text(0.1, 0.975, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),
                    transform=ax.transAxes, color='black', fontweight='bold',
                    fontsize=15, va='top')
        if val == 1:
            ax.text(0.6, 0.20, r'$\delta$' + ' = ' + '{0:.2f}'.format(x2[val]),
                    transform=ax.transAxes, color='black', fontweight='bold',
                    fontsize=15, va='top')
            ax.text(0.5, 0.35, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),
                    transform=ax.transAxes, color='black', fontweight='bold',
                    fontsize=15, va='top')
        if val == 2:
            ax.text(0.4, 0.175, r'$\delta$' + ' = ' +
                    '{0:.2f}'.format(x2[val]), transform=ax.transAxes,
                    color='black', fontweight='bold', fontsize=15, va='top')
            ax.text(0.3, 0.325, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),
                    transform=ax.transAxes, color='black', fontweight='bold',
                    fontsize=15, va='top')
        if val == 3:
            ax.text(0.6, 0.825, r'$\delta$' + ' = ' +
                    '{0:.2f}'.format(x2[val]), transform=ax.transAxes,
                    color='black', fontweight='bold', fontsize=15, va='top')
            ax.text(0.5, 0.975, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),
                    transform=ax.transAxes, color='black', fontweight='bold',
                    fontsize=15, va='top')

ax = fig.add_subplot(outer_grid[:, 2:])
ax.text(-0.20, 1.15, 'B', transform=ax.transAxes,
        color='black', fontweight='bold', fontsize=label_font, va='top')
ax.set_xlabel('Cumulative Dose (Gy)', fontsize=font, labelpad=40)
ax.set_ylabel('% Initial Tumor Volume', fontsize=font, labelpad=50)
axs = [ax]
for ax in axs:
    ax.set_xticks([])
    ax.set_yticks([])
    ax.patch.set_alpha(0.01)
    for key, spine in ax.spines.items():
        spine.set_visible(False)

plt.subplots_adjust(top=0.89, bottom=0.195, left=0.09,
                    right=0.985, hspace=0.2, wspace=0.2)

current_dir = os.getcwd()
parent_dir = os.path.abspath(os.path.join(current_dir, os.pardir))
folder_path = os.path.join(parent_dir, 'Figures')
os.makedirs(folder_path, exist_ok=True)
if direct:
    plt.savefig(os.path.join(folder_path, "Figure_4.png"), dpi=300, bbox_inches='tight')
if indirect:
    plt.savefig(os.path.join(folder_path, "Figure_5.png"), dpi=300, bbox_inches='tight')

plt.show()
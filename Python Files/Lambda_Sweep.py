## Figure 4: Lambda Parameter Sweep

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import style, cm, colors
from mpl_toolkits.axes_grid1.inset_locator import inset_axes, mark_inset
import matplotlib.gridspec as gridspec
import csv, os

style.use('default')

start_psi = 0.8
axis_thickness = 3

s1 = 'lam_sweep_direct_psi_0.7'
s2 = 'lam_sweep_indirect_psi_0.7'

s3 = 'lam_sweep_direct_psi_0.7_k'
s4 = 'lam_sweep_indirect_psi_0.7_k'

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

t = pd.read_csv(str(s1 + '_T.csv'))
t2 = pd.read_csv(str(s2 + '_T.csv'))
k = pd.read_csv(str(s3 + '_T.csv'))
k2 = pd.read_csv(str(s4 + '_T.csv'))

read_data = [t, t2]
k_data = [k, k2]
letter = ['A', 'B']
labels = ['K']
outer_grid = gridspec.GridSpec(2, 2, width_ratios=(2, 2))

fig = plt.figure(figsize=(6.8*2, 5))
axs = [plt.subplot(outer_grid[:, 0]), plt.subplot(outer_grid[:, 1])]

min_val, max_val = 0, 0.75
n = 20
orig_cmap = cm.YlGn_r
col = orig_cmap(np.linspace(min_val, max_val, n))

for x in range(2):
    t = read_data[x]
    k = k_data[x]
    ax = axs[x]

    ax.text(-0.35, 1.2, letter[x], transform=ax.transAxes,
        color='black', fontweight='bold', fontsize=30, va='top')
    
    cmap = colors.LinearSegmentedColormap.from_list(
    "mycmap", col, N=len(t.columns) - 1)
    stepsize = 0.01

    norm = colors.Normalize(vmin=0, vmax=stepsize * (len(t.columns) - 1))
    sm = plt.cm.ScalarMappable(norm=norm, cmap=cmap)

    for i in range(1, len(t.columns)):
        ax.plot((t.iloc[:, 0]) / 7, t.iloc[:, i], c=cmap(i))

    ax.plot((t.iloc[:, 0]) / 7, k.iloc[:, 1],
            label='K', c='darkorange', linewidth=2)
    
    cb = plt.colorbar(sm, ax=ax)
    labels = np.arange(0, stepsize * (len(t.columns) - 1), stepsize * 2).round(3)
    loc = labels + stepsize / 2
    cb.set_ticks(loc)
    cb.ax.set_yticklabels(["{:.2f}".format(i) for i in labels])
    cb.ax.tick_params(axis='both', which='major', labelsize=20)

    ax.text(1.05, 1.05, r'$\lambda$' +
            ' (' + r'day$\mathregular{^{-1}}$' + ')',
            transform=ax.transAxes, fontsize=15, color='black')

    ax.set_xlabel('Time on RT (week)', fontsize=20, labelpad=10)
    ax.set_ylabel('% Initial Tumor Volume', fontsize=20, labelpad=10)
    ax.tick_params(axis='both', which='major', labelsize=20)
    
    ax.set_xlim((0, 5))
    ax.set_ylim((0, 100 / start_psi + 40))
    ax.set_title(('DVR' if x == 0 else 'CCR') + ' Model', fontsize=20, pad=7.5)

    xmin, xmax = ax.get_xlim()
    ymin, ymax = ax.get_ylim()

    ax.set_xticks(np.arange(0, xmax + 1, 1))
    ax.set_yticks(np.linspace(0, 150, 4))
    ax.tick_params(axis='both', which='both', pad=10, width=axis_thickness)

    if x == 0:
        loc = [4.3, 135]
    if x == 1:
        loc = [2.3, 20]
    ax.text(loc[0], loc[1], 'K',
            color='darkorange', fontweight='bold', fontsize=20, va='top')

    for axis in ['bottom', 'left']:
        ax.spines[axis].set_linewidth(axis_thickness)

    for axis in ['top', 'right']:
        ax.spines[axis].set_linewidth(0)

####################

subax1 = inset_axes(axs[1], width=1.3, height=0.9)
subax1.tick_params(width=axis_thickness)
subax1.tick_params(axis='both', which='major')

red_time = (t2.iloc[0:14*7, 0]) / 7
red_vals = (t2.iloc[0:14*7, 1:])

for i in range(0, len(red_vals.columns)):
    subax1.plot(red_time, red_vals.iloc[:, i], c=cmap(i))

subymin, subymax = subax1.get_ylim()
subax1.set_ylim([100, subymax])
subxmin, subxmax = subax1.get_xlim()
subax1.set_xlim([0, subxmax])
subax1.set_xticks(np.linspace(0, 0.6, 3))

subax1.plot(red_time, k2.iloc[0:14*7, 1:],
            label='K', c='darkorange', linewidth=2)

for axis in ['top', 'right']:
    subax1.spines[axis].set_linewidth(0)
for axis in ['bottom', 'left']:
    subax1.spines[axis].set_linewidth(axis_thickness)

ax.indicate_inset_zoom(subax1, edgecolor="black", linewidth=2, alpha=0.25)
mark_inset(ax, subax1, loc1=2, loc2=4, fc="none", ec='0.5', alpha=0.2)

plt.subplots_adjust(top=0.86, bottom=0.173, left=0.107,
                    right=0.985, hspace=0.076, wspace=0.5)

current_dir = os.getcwd()
parent_dir = os.path.abspath(os.path.join(current_dir, os.pardir))
folder_path = os.path.join(parent_dir, 'Figures')
os.makedirs(folder_path, exist_ok=True)
plt.savefig(os.path.join(folder_path, "Figure_4.png"), bbox_inches='tight', dpi=300)

# plt.show()
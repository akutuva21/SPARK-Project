## Figure 2: Death Parameter Sweep (Alpha and Delta)

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import style, cm, colors
import matplotlib.gridspec as gridspec
import csv, os

style.use('default')
directory = ''

s = directory + 'l=0.1,psi=0.9,a'
s2 = directory + 'l=0.1,psi=0.9,d'

files = [s, s2]
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

t = pd.read_csv(str(s + '_T.csv'))
t2 = pd.read_csv(str(s2 + '_T.csv'))

read_data = [t, t2]
letter = ['A', 'B']
outer_grid = gridspec.GridSpec(2, 3, width_ratios=(2, 0.1, 2))

fig = plt.figure(figsize=(12, 6))
axs = [plt.subplot(outer_grid[:, 0]), plt.subplot(outer_grid[:, 2])]
axis_thickness = 3

min_val, max_val = 0, 0.65
n = 20
orig_cmap = cm.Blues_r
col = orig_cmap(np.linspace(min_val, max_val, n))

for x in range(2):
    t = read_data[x]
    ax = axs[x]

    ax.text(-0.35, 1.2, letter[x], transform=ax.transAxes,
            color='black', fontweight='bold', fontsize=30, va='top')

    cmap = colors.LinearSegmentedColormap.from_list(
        "mycmap", col, N=len(t.columns) - 1)
    min_val, max_val = 0, 0.65
    n = 10
    stepsize = 0.02
    m = len(t.columns) - 1

    orig_cmap = cm.Blues_r
    col = orig_cmap(np.linspace(min_val, max_val, n))
    cmap = colors.LinearSegmentedColormap.from_list("mycmap", col, N=m)
    norm = colors.Normalize(vmin=0, vmax=stepsize * (len(t.columns) - 1))
    sm = plt.cm.ScalarMappable(cmap=cmap, norm=norm)

    for i in range(1, len(t.columns)):
        ax.plot((t.iloc[:, 0]) / 7, t.iloc[:, i], c=cmap(i))

    cb = plt.colorbar(sm, ax=ax)
    labels = np.linspace(0, stepsize * (len(t.columns) - 2), 3)

    loc = labels + stepsize / 2
    cb.set_ticks(loc)
    cb.ax.set_yticklabels(["{:.1f}".format(i) for i in labels])
    if x == 0:
        cb.ax.set_title('           ' + r'$\alpha$' + ' (' +
                        r'Gy$\mathregular{^{-1}}$' + ')', pad=10, fontsize=20)
    if x == 1:
        cb.ax.set_title(r'$\delta$', pad=10, fontsize=20)

    cb.ax.tick_params(axis='both', which='major', labelsize=20)
    # cb.ax.invert_yaxis()

    ax.set_xlabel('Time on RT (week)', fontsize=20, labelpad=10)
    ax.set_ylabel('% Initial Tumor Volume', fontsize=20, labelpad=10)
    ax.tick_params(axis='both', which='major', labelsize=20)

    ax.set_xlim((0, 5))
    ax.set_ylim((0, 120))

    xmin, xmax = ax.get_xlim()
    # ax.axvspan(xmin, xmax, facecolor='lightgray', alpha=0.5)
    # for time in np.arange(xmin, xmax, 1/7 * 1/24):
    #     weekend = (int) (time * 7 * 24) % (24 * 7)
    #     if weekend <= 120:
    #         ax.axvspan(time, time + 1/24, facecolor="w", alpha=0.5)

    ax.set_xticks(np.arange(0, xmax + 1, 1))
    ax.set_yticks(np.linspace(0, 100, 3))
    ax.tick_params(axis='both', which='both', pad=10, width=axis_thickness)

    for axis in ['bottom', 'left']:
        ax.spines[axis].set_linewidth(axis_thickness)

    for axis in ['top', 'right']:
        ax.spines[axis].set_linewidth(0)

plt.subplots_adjust(top=0.86, bottom=0.173, left=0.107,
                    right=0.966, hspace=0.076, wspace=0.3)
fig.set_size_inches(6.8*2, 5)

current_dir = os.getcwd()
parent_dir = os.path.abspath(os.path.join(current_dir, os.pardir))
folder_path = os.path.join(parent_dir, 'Figures')
os.makedirs(folder_path, exist_ok=True)
plt.savefig(os.path.join(folder_path, "Figure_2.png"), dpi=300, bbox_inches='tight')

plt.show()
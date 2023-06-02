## Figure 1: Logistic Growth Model and Direct and Indirect Models

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import style
import matplotlib.gridspec as gridspec
import csv

style.use('default')

outer_grid = gridspec.GridSpec(1, 3, width_ratios=(2, 2, 2))
fig = plt.figure(figsize=(15, 5))
axs = [
    plt.subplot(outer_grid[:, 0]),
    plt.subplot(outer_grid[:, 1]),
    plt.subplot(outer_grid[:, 2])
]

start_psi = 0.8
start_psi = 1
threshold = 0.322
x = 1
axis_thickness = 3

a1 = 'Logistic_Volume'
b1 = 'Logistic_k'
c1 = 'Logistic All_Values'
a2 = 'Direct_Volume'
b2 = 'Direct_k'
c2 = 'Direct All_Values'
a3 = 'Indirect_Volume'
b3 = 'Indirect_k'
c3 = 'Indirect All_Values'

files = [a1, b1, a2, b2, a3, b3]
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

volume = [pd.read_csv(a1 + "_T.csv"), pd.read_csv(a2 + "_T.csv"), pd.read_csv(a3 + "_T.csv")]
k_vals = [pd.read_csv(b1 + "_T.csv"), pd.read_csv(b2 + "_T.csv"), pd.read_csv(b3 + "_T.csv")]
all_values = [pd.read_csv(c1 + ".csv"), pd.read_csv(c2 + ".csv"), pd.read_csv(c3 + ".csv")]
letter = ['A', 'B', 'C']

for i in range(3):
    ax = axs[i]
    logistic = False
    direct = False
    indirect = False

    t = volume[i]
    t2 = k_vals[i]
    t3 = all_values[i]

    lam = t3.iloc[:, 0]
    alpha = t3.iloc[:, 1]
    delta = t3.iloc[:, 2]
    psi = t3.iloc[:, 3]

    if i == 0:
        logistic = True
    if i == 1:
        direct = True
    if i == 2:
        indirect = True

    ax.plot((t.iloc[:, 0]) / 7, t.iloc[:, 1],
            label=r'$V$', c='dodgerblue', linewidth=3)

    ax.plot((t2.iloc[:, 0]) / 7, t2.iloc[:, 1],
            label=r'$K$', c='darkorange', linewidth=3)

    ax.set_xlim((0, 4))
    ax.set_ylim((0, (100 / start_psi) + 5))
    ymin, ymax = ax.get_ylim()
    xmin, xmax = ax.get_xlim()

    props = dict(boxstyle='square', facecolor='none',
                 edgecolor='none', pad=0.2)  # lightgray
    props_blank = dict(boxstyle='square', facecolor='none',
                       edgecolor='none', pad=0.2)
    props2 = dict(boxstyle='square', facecolor='none',
                  edgecolor='none', pad=0.2)  # lightgray

    ax.set_xticks(np.arange(0, xmax + 1, 1))
    ax.set_yticks(np.linspace(0, 100, 3))

    if i == 0:
        x_label = 'Time'
        y_label = 'Tumor Volume'
        ax.legend(loc='lower right', fontsize=20)
        ax.set_xticks([])
        ax.set_yticks([])

    else:
        x_val = [x/7 for x in range(0, 28) if x % 7 < 5]
        y_val = [105] * len(x_val)
        ax.scatter(x_val, y_val, marker=r'$\downarrow$', c='green',
                   s=300, linewidth=0.25, edgecolors='black', zorder=5)

        title = ('DVR' if direct else 'CCR') + ' Model'
        x_label = 'Time on RT (week)'
        y_label = '% Initial Tumor Volume'

        ax.tick_params(axis='both', which='major', labelsize=20)
        ax.tick_params(axis='both', which='both', pad=10, width=axis_thickness)

        alpha_text = r'$\alpha=%.2f$' % (
            alpha[x - 1]) + ' ' + '$Gy^{-1}$' if direct else ''
        delta_text = r'$\delta=%.2f$' + str(delta[x - 1]) if indirect else ''
        lambda_text = '\n' + \
            r'$\lambda=%.2f$' % (lam[x - 1]) + ' ' + '$day^{-1}$' + '\n'
        psi_text = r'$\mathrm{PSI}=%.2f$' % (psi[x - 1])

        textstr_1 = ''.join((alpha_text, delta_text, lambda_text, psi_text))
        textstr_2 = ''.join(
            (r'$\delta=%.2f$' + str(delta[x - 1]), lambda_text, psi_text))
        ax.text(0.05, 0.30 if direct else 0.28, textstr_1, fontsize=15,
                transform=ax.transAxes, verticalalignment='top',
                bbox=props if direct else props_blank, zorder=4)
        if indirect:
            ax.text(0.05, 0.30, textstr_2, color='white', fontsize=15,
                    transform=ax.transAxes, verticalalignment='top',
                    bbox=props2, zorder=3)
        ax.set_title(title, fontsize=20, pad=7.5)

    ax.set_xlabel(x_label, fontsize=20, labelpad=10)
    ax.set_ylabel(y_label, fontsize=20, labelpad=10)
    for axis in ['bottom', 'left']:
        ax.spines[axis].set_linewidth(axis_thickness)
    for axis in ['top', 'right']:
        ax.spines[axis].set_linewidth(0)

plt.tight_layout()
plt.subplots_adjust(top=0.8)
axs[0].set_title('Logistic Tumor Growth', fontsize=20, pad=28, fontweight='bold')
axs[0].text(-0.15, 1.2, 'A', fontsize=30, transform=axs[0].transAxes, fontweight='bold')

# Add a subtitle between subplots 2 and 3
subtitle_ax = fig.add_subplot(outer_grid[:, 1:])
subtitle_ax.set_title('RT Response Models', fontsize=20, pad=28, fontweight='bold')
subtitle_ax.text(-0.15, 1.2, 'B', fontsize=30, transform=subtitle_ax.transAxes, fontweight='bold')
subtitle_ax.axis('off')

plt.show()
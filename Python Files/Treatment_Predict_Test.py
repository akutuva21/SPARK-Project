import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.patches as mpatches
# from matplotlib import cm
from matplotlib import style

style.use('default')

# psi = 0.9
threshold = 0.322

s = 'Volume_Data.csv'
t = pd.read_csv(s)
s2 = 'k_vals.csv'
t2 = pd.read_csv(s2)
s3 = '../SPARK Project/All_Values.csv'
t3 = pd.read_csv(s3)
fig, ax = plt.subplots()

lam = t3.iloc[:, 0]
alpha = t3.iloc[:, 1]
delta = t3.iloc[:, 2]
psi = t3.iloc[:, 3]
# k = 100.0 / psi;
cumul = t3.iloc[:, 4]
frac_size = t3.iloc[:, 5]
num = cumul / frac_size
gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

x = 1
plt.plot((t2.iloc[:, 0]) / 7, t2.iloc[:, x],
         label='K (Carrying Capacity)', c='darkorange', linewidth=5)

x = (t.iloc[:, 0]) / 7

# condition = t3[(psi > 0.97) & (delta < 0.02)]
# reduced = list(condition.index.values + 1)
# t = t.iloc[:,reduced]

for i in np.arange(1, len(t.columns)):
    loc = t.iloc[:, i]
    if loc[len(loc.dropna()) - 1] < (1 - threshold) * loc[1]:
        c = 'g'
    else:
        c = 'r'
    plt.plot(x, loc, linewidth=1, c=c, zorder=2)

green_patch = mpatches.Patch(color='g', label='Locoregional Control')
red_patch = mpatches.Patch(color='r', label='Locoregional Failure')

ax.set_xlabel('Time', fontsize=20, labelpad=15)
ax.set_ylabel('Tumor Volume', fontsize=20, labelpad=15)
plt.xlim((0, 7))
plt.ylim((0, 100))
ymin, ymax = ax.get_ylim()
xmin, xmax = ax.get_xlim()
ax.set_aspect(xmax / ymax)
# ax.axvspan(xmin, xmax, facecolor='lightgray', alpha=0.5)

for time in np.arange(xmin, xmax, 1/7 * 1/24):
    weekend = (int)(time * 24 * 7) % (24 * 7)
    if weekend < 120:
        ax.axvspan(time, time + 1/24, facecolor='white', alpha=0.5)

ax.set_xticks([])
ax.set_yticks([])
# ax.set_yticks(np.linspace(0, 100, 3))
ax.tick_params(axis='both', which='both', pad=10)

for axis in ['bottom', 'left']:
    ax.spines[axis].set_linewidth(3)

for axis in ['top', 'right']:
    ax.spines[axis].set_linewidth(0)
ax.tick_params(width=2)

fig.set_size_inches(6.8, 5, forward=True)
plt.tight_layout()
plt.show()
# plt.ylabel('Normalized Tumor Volume', fontsize = 25,
#            fontweight = 2, labelpad = 12)
# plt.legend(handles=[green_patch, red_patch], fontsize = 20,
#            loc='upper right')
# ax.set_aspect(1/20)
# plt.legend(fontsize = 12)

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.patches as mpatches
# from matplotlib import cm
from matplotlib import style, cm

style.use('default')

# psi = 0.9
threshold = 0.322

s = 'Volume_Data.csv'
t = pd.read_csv(s)
# s3 = '../SPARK Project/All_Values.csv'
# t3 = pd.read_csv(s3)
fig, ax = plt.subplots(figsize=(8, 8))
ax2 = ax.twiny()

# lam = t3.iloc[:,0]
# alpha = t3.iloc[:,1]
# delta = t3.iloc[:,2]
# psi = t3.iloc[:,3]
# cumul = t3.iloc[:,4]
# frac_size = t3.iloc[:,5]
# num = cumul / frac_size
# gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

x = (t.iloc[:, 0]) / 7
numpts = 0

# condition = t3[(psi > 0.97) & (delta < 0.02)]
# reduced = list(condition.index.values + 1)
# t = t.iloc[:,reduced]

num_to_plot = 50
# len(t.columns)
for i in range(1, num_to_plot):
    c = 'g'
    plt.plot(x, t.iloc[:, i], linewidth=1, c=c, zorder=2)


ax.set_xlabel('Time (Weeks)', fontsize=20, labelpad=15)
ax.set_ylabel('Tumor Volume', fontsize=20, labelpad=15)
ax.set_xlim((0, 7))
ax.set_ylim((0, 150))
ymin, ymax = ax.get_ylim()
xmin, xmax = ax.get_xlim()

ax2.set_xlim([0, xmax])
xrange = [x for x in np.arange(xmin, xmax, 1/7) if (x * 7) % (7) == 0]
ax2.set_xticks(xrange)
ax2.set_xticklabels(str(x * 2 * 5) for x in xrange)
ax2.tick_params(axis='both', which='major', labelsize=15, pad=5, width=3)
ax2.set_xlabel('RT Dose Administered (Gy)', fontsize=20, labelpad=20)

for axis in ['top', 'right']:
    ax2.spines[axis].set_linewidth(3)

for time in np.arange(xmin, xmax, 1/7 * 1/24):
    weekend = (int)(time * 24 * 7) % (24 * 7)
    if weekend < 120:
        ax.axvspan(time, time + 1/24, facecolor='white', alpha=0.5)

ax.set_xticks([x for x in np.arange(xmin, xmax)])
ax.tick_params(axis='both', which='major', labelsize=15, pad=5, width=3)
ax.set_yticks([])
# ax.set_aspect(xmax / ymax)

ax.tick_params(axis='both', which='both', pad=10)
plt.vlines(max(x), 0, ymax, linestyles='dotted',
           color='black', linewidth=5, zorder=3)
plt.hlines((1 - threshold) * 100, 0, xmax, colors='k',
           linestyles='dotted', color='black', linewidth=5, zorder=3)

for axis in ['bottom', 'left']:
    ax.spines[axis].set_linewidth(3)

for axis in ['top', 'right']:
    ax.spines[axis].set_linewidth(0)
ax.tick_params(width=2)

# fig.set_size_inches(6.8, 5, forward = True)
plt.tight_layout()
plt.show()

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style, cm, colors

fig = plt.figure()
ax = fig.add_subplot()
# ax = fig.add_subplot(projection='3d')

style.use('default')

s3 = '../SPARK Project/frac_0.1-30_psi_.6_1_0.07_a_0.11.csv'
# s3 = '../SPARK Project/frac_0.1-30_psi_.6_1_0.01_d_0.09.csv'
t3 = pd.read_csv(s3)
print(t3)

lam = t3.iloc[:, 0]
alpha = t3.iloc[:, 1]
delta = t3.iloc[:, 2]
psi = t3.iloc[:, 3]
cumul = t3.iloc[:, 4]
frac_size = t3.iloc[:, 5]
t3['Number of Fractions'] = cumul / frac_size
num = t3['Number of Fractions']
t3['Gamma'] = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))
gamma = t3['Gamma']

print(t3)

m = 8
min_val, max_val = 0, 0.6
n = 20
orig_cmap = cm.viridis_r
col = orig_cmap(np.linspace(min_val, max_val, m))
cmap = colors.LinearSegmentedColormap.from_list("mycmap", col)

mask = (cumul >= 0) & (alpha == 0.07) & ((psi == 0.7) | (psi == 0.8) | (psi == 0.9)) & (frac_size <= 12) # | (psi == 0.8)
# mask = (psi == 0.6) & (frac_size <= 12) & (delta == 0.08)

sc = ax.scatter(x=frac_size[mask], y=num[mask], c=cumul[mask], cmap=cmap, edgecolors='none')

# correlation_xy = np.corrcoef(x, y)[0, 1]
# print(correlation_xy**2)

""" title = plt.title("\n".join(wrap("Minimum Number of Doses after Changing" +
                                 "Fraction Size (Gy) and Initial PSI to" +
                                 "Achieve LRC", width=60)),
                  fontsize=20)
title = plt.title("\n".join(wrap("Effect of Changing Fraction Size (Gy)" +
                                 "on Number of Fractions",
                                 width=60)),
                  fontsize=20) """
# title.set_y(5)
plt.xlabel("Fraction Size (Gy)", fontsize=20, labelpad=10)
plt.ylabel("Number of Fractions", fontsize=20, labelpad=10)
plt.ylim(0, 45)
ax.tick_params(axis='both', which='major', labelsize=15)

# cb = plt.colorbar(sm, ax=ax)
# step = 2
# norm = colors.Normalize(vmin= 0, vmax= step * m)
# sm = plt.cm.ScalarMappable(norm = norm, cmap = cmap)
cb = fig.colorbar(sc, format='%.2f')
# labels = np.linspace(4, 4 + step * m, m + 1)
# loc = labels - 3 * step / 2
# cb.set_ticks(loc)
# tick = cb.set_ticklabels(labels)

for axis in ['bottom', 'left']:
    ax.spines[axis].set_linewidth(3)

for axis in ['top', 'right']:
    ax.spines[axis].set_linewidth(0)
ax.tick_params(width=2)

cb.ax.set_ylabel('PSI', labelpad=10, fontsize=20)

cb.ax.tick_params(axis='both', which='major', labelsize=15)
fig.set_size_inches(6.8, 5, forward=True)
plt.tight_layout()
plt.savefig('frac_num_psi.png', dpi=300)
plt.show()

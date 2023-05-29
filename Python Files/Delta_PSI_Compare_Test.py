import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style, cm, colors
from textwrap import wrap

style.use('ggplot')

threshold = 0.322

s3 = 'All_Values.csv'
t3 = pd.read_csv(s3)

lam = t3.iloc[:, 0]
alpha = t3.iloc[:, 1]
delta = t3.iloc[:, 2]
psi = t3.iloc[:, 3]
k = 100.0 / psi
cumul = t3.iloc[:, 4]
frac_size = t3.iloc[:, 5]
num = cumul / frac_size
gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

###############################################################################

fig = plt.figure()
ax = fig.add_subplot(111, label="1")
ax2 = ax.twinx()
ax.grid(False)
ax2.grid(False)
cmap = cm.get_cmap('viridis_r')

x = frac_size
y = psi
c = cumul
sc = ax.scatter(x, y, c=c, cmap=cmap)

x = frac_size
y = delta
# c = cumul
sc2 = ax2.scatter(x, y)

correlation_xy = np.corrcoef(x, y)[0, 1]
print(correlation_xy**2)

title = plt.title("\n".join(wrap("Effects of Changing Fraction Size (Gy)" +
                                 "and Initial Proliferation Saturation on" +
                                 "Minimum Dose to Reach 32.2% Threshold (Gy)",
                                 width=80)),
                  fontsize=20)
title.set_y(5)
ax.set_xlabel("Fraction Size (Gy)", fontsize=20, labelpad=10)
ax.set_ylabel('PSI (Proliferation Saturation)', fontsize=20, labelpad=20)
ax.set_ylim(0, 1)
ax.tick_params(axis='both', which='major', labelsize=15)

ax2.yaxis.tick_right()
ax2.set_ylabel(r'$\delta$', fontsize=20, labelpad=20)
ax2.yaxis.set_label_position('right')
# ax2.set_xlabel("Fraction Size (Gy)", fontsize = 20, labelpad = 20)
ax2.tick_params(axis='both', which='major', labelsize=15)

# norm = colors.Normalize(vmin= 0, vmax= (max(cumul)))
# sm = plt.cm.ScalarMappable(norm = norm, cmap = cmap)
norm = colors.Normalize(vmin=0, vmax=(max(cumul)))
sm = plt.cm.ScalarMappable(norm=norm, cmap=cmap)
cb = plt.colorbar(sm, pad=0.1)
cb.ax.set_ylabel('Minimum Cumulative Dose (Gy)', labelpad=20, fontsize=20)
cb.ax.tick_params(axis='both', which='major', labelsize=15)

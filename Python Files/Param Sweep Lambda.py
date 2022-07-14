import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import style, cm, colors
import matplotlib.patches as mpatches
from matplotlib.lines import Line2D
from mpl_toolkits.axes_grid1.inset_locator import inset_axes
from mpl_toolkits.axes_grid1.inset_locator import mark_inset

style.use('default')

start_psi = 0.8
axis_thickness = 3;

s = '../SPARK Project/Paper Edited Figures/lam_sweep_direct_psi_0.7.csv'
t = pd.read_csv(str(s))
s2 = '../SPARK Project/Paper Edited Figures/lam_sweep_indirect_psi_0.7.csv'
t2 = pd.read_csv(str(s2))

l = '../SPARK Project/Paper Edited Figures/lam_sweep_direct_psi_0.7_k.csv'
k = pd.read_csv(l)
l2 = '../SPARK Project/Paper Edited Figures/lam_sweep_indirect_psi_0.7_k.csv'
k2 = pd.read_csv(l2)

min_val, max_val = 0,0.5
n = 20
orig_cmap = cm.YlGn_r
col = orig_cmap(np.linspace(min_val, max_val, n))
cmap = colors.LinearSegmentedColormap.from_list("mycmap", col, N = len(t.columns) - 1)

#cmap = cm.get_cmap('copper_r', len(t.columns) - 1)

fig, axs = plt.subplots(1, 2)

for i in range(1, len(t.columns)):
    axs[0].plot((t.iloc[:, 0]) / 7, t.iloc[:, i], c = cmap(i))
    
axs[0].plot((t.iloc[:, 0]) / 7, k.iloc[:, 1], label = 'K', c= 'darkorange', linewidth = 2)
#axs[0].set_title("LQ PSI Model", fontsize = 20, pad = 10) 

stepsize = 0.01

norm = colors.Normalize(vmin= 0, vmax= stepsize * (len(t.columns) - 1))
sm = plt.cm.ScalarMappable(norm = norm, cmap = cmap)
cb = plt.colorbar(sm, ax=axs[0], format = '%.3f', fraction = 0.046, pad = 0.04)
labels = np.arange(0, stepsize * (len(t.columns) - 1), stepsize * 2)
loc = labels + stepsize / 2
cb.set_ticks(loc)
cb.ax.set_yticklabels([i for i in labels])
#cb.ax.set_title('           ' + r'$\lambda$'+ ' (' + r'day$\mathregular{^{-1}}$' + ')', pad = 15, fontsize = 15)
#cb.ax.set_xlabel(r'$\lambda$', fontsize = 20, labelpad = 15)
cb.ax.tick_params(axis = 'both', which = 'major', labelsize = 17)

################
#cmap = cm.get_cmap('winter', len(t2.columns) - 1)

for i in range(1, len(t2.columns)):
    axs[1].plot((t2.iloc[:, 0]) / 7, t2.iloc[:, i], c = cmap(i))
    
axs[1].plot((t2.iloc[:, 0]) / 7, k2.iloc[:, 1], label = 'K (Carrying Capacity)', c= 'darkorange', linewidth = 2)
#axs[1].set_title(r'$\Delta$' + "K PSI Model", fontsize = 20, pad = 10)

cmap = colors.LinearSegmentedColormap.from_list("mycmap", col, N = len(t2.columns) - 1)

norm = colors.Normalize(vmin= 0, vmax= stepsize * (len(t2.columns) - 1))
sm = plt.cm.ScalarMappable(norm = norm, cmap = cmap)

cb = plt.colorbar(sm, ax=axs[1], format = '%.5f', fraction = 0.046, pad = 0.04)
labels = np.arange(0, stepsize * (len(t2.columns) - 1), stepsize * 2).round(3)
loc = labels + stepsize / 2
cb.set_ticks(loc)
cb.ax.set_yticklabels([i for i in labels])
#cb.ax.set_xlabel(r'$\lambda$', fontsize = 20, labelpad = 15)
cb.ax.tick_params(axis = 'both', which = 'major', labelsize = 17)
#cb.ax.invert_yaxis()

#ins = axs[1].inset_axes([0.75,0.75,0.2,0.2])

#plt.colorbar(im, cax=cax)
red_patch = mpatches.Patch(color='darkorange', label = 'K')
colors = ['darkorange']
lines = [Line2D([0], [0], color=c, linewidth=2) for c in colors]
labels = ['K']
letter = ['A', 'B']

for x in range(2):
    ax = axs[x]
    
    ax.text(-0.45, 1.15, letter[x], transform=ax.transAxes,
      color='black', fontweight = 'bold', fontsize = 30, va='top')
    
    ax.text(1.05, 1.05, r'$\lambda$' + ' (' + r'day$\mathregular{^{-1}}$' + ')', 
            transform=ax.transAxes, fontsize = 15, color = 'black')
        
    ax.set_xlabel('Time on RT (week)', fontsize = 20)
    ax.set_ylabel('% Initial Tumor Volume', fontsize = 20)
    ax.tick_params(axis = 'both', which = 'major', labelsize = 20, pad = 10)
    ax.set_xlim((0, 5))
    ax.set_ylim((0, 100 / start_psi + 40))
    
    xmin, xmax = ax.get_xlim()
    ymin, ymax = ax.get_ylim()
    ax.set_aspect(xmax / ymax)
    
    # ax.axvspan(xmin, xmax, facecolor='lightgray', alpha=0.5)
    # for time in np.arange(xmin, xmax, 1/7 * 1/24):
    #     weekend = (int) (time * 7 * 24) % (24 * 7)
    #     if weekend <= 120:
    #         ax.axvspan(time, time + 1/24, facecolor="w", alpha=0.5)
    ax.set_xticks(np.arange(0, xmax + 1, 1))
    ax.set_yticks(np.linspace(0, 150, 4))

    #ax.legend(lines, labels, loc = 'lower left', fontsize = 15)
    
    if x == 0:
        loc = [4.3, 135]
    if x == 1:
        loc = [2.3, 20]
    ax.text(loc[0], loc[1], 'K',
      color='darkorange', fontweight = 'bold', fontsize = 20, va='top')
    
    for axis in ['bottom','left']:
      ax.spines[axis].set_linewidth(axis_thickness)
      
    for axis in ['top','right']:
      ax.spines[axis].set_linewidth(0)
    ax.tick_params(width = axis_thickness)

subax1 = inset_axes(axs[1], width=1.3, height=0.9)
subax1.tick_params(width = axis_thickness)
subax1.tick_params(axis = 'both', which = 'major')

red_time = (t2.iloc[0:14*7, 0]) / 7
red_vals = (t2.iloc[0:14*7, 1:])

for i in range(0, len(red_vals.columns)):
    subax1.plot(red_time, red_vals.iloc[:, i], c = cmap(i))

subymin, subymax = subax1.get_ylim()
subax1.set_ylim([100, subymax])
subxmin, subxmax = subax1.get_xlim()
subax1.set_xlim([0, subxmax])
subax1.set_xticks(np.linspace(0, 0.6, 3))

subax1.plot(red_time, k2.iloc[0:14*7, 1:], label = 'K', c= 'darkorange', linewidth = 2)
  
for axis in ['top','right']:
  subax1.spines[axis].set_linewidth(0)
for axis in ['bottom','left']:
  subax1.spines[axis].set_linewidth(axis_thickness);

ax.indicate_inset_zoom(subax1, edgecolor="black", linewidth = 2, alpha = 0.25)
mark_inset(ax, subax1, loc1=2, loc2=4, fc="none", ec='0.5', alpha = 0.2)
fig.set_size_inches(6.8*2, 5)
    
fig.tight_layout()
plt.show()
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import style, cm, colors
from mpl_toolkits.axes_grid1 import make_axes_locatable
style.use('default')

s = 'Param Sweep New/l=0.1,psi=0.9,d.csv'
t = pd.read_csv(str(s))
s2 = 'Param Sweep New/l=0.1,psi=0.9,a.csv'
t2 = pd.read_csv(str(s2))
#cmap = cm.get_cmap('Blues', len(t.columns) - 1)

min_val, max_val = 0,0.65
n = 20
orig_cmap = cm.Blues_r
col = orig_cmap(np.linspace(min_val, max_val, n))
cmap = colors.LinearSegmentedColormap.from_list("mycmap", col, N = len(t.columns) - 1)

# fig, axs = plt.subplots(1, 2)

# for i in range(1, len(t.columns)):
#     axs[0].plot((t.iloc[:, 0] - 1) / 7, t.iloc[:, i], c = cmap(i))

# stepsize = 0.03

# # norm = colors.Normalize(vmin= 0, vmax= stepsize * (len(t.columns) - 1))
# # sm = plt.cm.ScalarMappable(cmap = cmap, norm = norm)
# # cb = plt.colorbar(sm, ax=axs[0])
# # cmap = cmap.reversed();
# # labels = np.arange(0, stepsize * (len(t.columns) - 1), stepsize)

# # loc = labels + stepsize / 2
# # cb.set_ticks(loc)
# # cb.ax.set_yticklabels(["{:.2f}".format(i) for i in labels])
# # cb.ax.set_ylabel(r'$\alpha$', labelpad = 15, rotation = 0, fontsize = 20)
# # cb.ax.tick_params(axis = 'both', which = 'major', labelsize = 20)
# # cb.ax.invert_yaxis()

# for i in range(1, len(t2.columns)):
#     axs[1].plot((t2.iloc[:, 0] - 1) / 7, t2.iloc[:, i], c = cmap(i))
    
# stepsize = 0.02

# # norm = colors.Normalize(vmin= 0, vmax= stepsize * (len(t2.columns) - 1))
# # sm = plt.cm.ScalarMappable(cmap = cmap, norm = norm)
# # cb = plt.colorbar(sm, ax=axs[1])
# # cmap = cmap.reversed();
# # labels = np.arange(0, stepsize * (len(t2.columns) - 1), stepsize)

# # loc = labels + stepsize / 2
# # cb.set_ticks(loc)
# # cb.ax.set_yticklabels(["{:.2f}".format(i) for i in labels])
# # cb.ax.set_ylabel(r'$\delta$', labelpad = 15, rotation = 0, fontsize = 20)
# # cb.ax.tick_params(axis = 'both', which = 'major', labelsize = 20)
# # cb.ax.invert_yaxis()

# for ax in axs.flat:
#     ax.set_xlabel('Time (Weeks)', fontsize = 20)
#     ax.set_ylabel('Tumor Volume', fontsize = 20)
#     ax.tick_params(axis = 'both', which = 'major', labelsize = 20)
#     xmin, xmax = ax.get_xlim()
#     ax.axvspan(xmin, xmax, facecolor='lightgray', alpha=0.5)
#     for time in np.arange(xmin, xmax, 1/7 * 1/24):
#         weekend = (int) (time * 7 * 24) % (24 * 7)
#         if weekend <= 120:
#             ax.axvspan(time, time + 1/24, facecolor="w", alpha=0.5)
#     ax.set_xlim((0, 5))
#     ax.set_ylim((0, 120))
#     ax.set_aspect(5 / 120)
#     ax.set_xticks(np.arange(0, xmax + 1, 1))
#     ax.set_yticks(np.linspace(0, 100, 3))
#     ax.tick_params(axis = 'both', which = 'both', pad = 10, width = 3)
    
#     for axis in ['bottom','left']:
#       ax.spines[axis].set_linewidth(3)
      
#     for axis in ['top','right']:
#       ax.spines[axis].set_linewidth(0)

# fig.set_size_inches(13.6, 5, forward = True)
# plt.tight_layout()
# plt.show()

# #plt.title('Projected Tumor Volumes Over Time Following Changes in ' + r'$\delta$' + ' Death Parameter', fontsize = 22)
# #plt.tight_layout()
# plt.show()

##############################################################################
fig, ax = plt.subplots()

s = 'Param Sweep New/l=0.1,psi=0.9,d.csv'
t = pd.read_csv(str(s))
s2 = 'Param Sweep New/l=0.1,psi=0.9,a.csv'
#s2 = 'Volume_Data.csv'
t2 = pd.read_csv(str(s2))
#cmap = cm.get_cmap('Blues', len(t.columns) - 1)

min_val, max_val = 0,0.65
n = 10
stepsize = 0.02
m = len(t2.columns) - 1

orig_cmap = cm.Blues_r
col = orig_cmap(np.linspace(min_val, max_val, n))
cmap = colors.LinearSegmentedColormap.from_list("mycmap", col, N = m)
norm = colors.Normalize(vmin= 0, vmax= stepsize * (len(t2.columns) - 1))
sm = plt.cm.ScalarMappable(cmap = cmap, norm = norm)

for i in range(1, len(t2.columns)):
    ax.plot((t2.iloc[:, 0] - 1)/ 7, t2.iloc[:, i], c = cmap(i))
    #ax.plot((t2.iloc[:, 0])/ 7, t2.iloc[:, i], c = cmap(i / (len(t2.columns) + 1)))
    #print(cmap(i / (len(t2.columns) + 2)))

#divider = make_axes_locatable(ax)
#cax = divider.append_axes("right", size="10%", pad=0.05)

# left, bottom, width, height - rectangle
cb = plt.colorbar(sm, fraction = 0.046, pad = 0.04)
#labels = np.arange(0, stepsize * (len(t2.columns) - 1), stepsize)
#labels = np.arange(0, stepsize * (len(t2.columns) - 1), stepsize * 3)
labels = np.linspace(0, stepsize * (len(t2.columns) - 2), 3)

loc = labels + stepsize / 2
cb.set_ticks(loc)
cb.ax.set_yticklabels(["{:.2f}".format(i) for i in labels])
#cb.ax.set_title(r'$\delta$', pad = 10, fontsize = 20)
cb.ax.set_title('           ' + r'$\alpha$' + ' (' + r'Gy$\mathregular{^{-1}}$' + ')', pad = 10, fontsize = 15)

cb.ax.tick_params(axis = 'both', which = 'major', labelsize = 20)
#cb.ax.invert_yaxis()

#ax.set_title(r'$\Delta$' + "K PSI Model", fontsize = 20, pad = 10)
#ax.set_title("LQ PSI Model", fontsize = 20, pad = 10)

ax.set_xlabel('Time on RT (Weeks)', fontsize = 20, labelpad = 10)
ax.set_ylabel('% Initial Tumor Volume', fontsize = 20, labelpad = 10)
ax.tick_params(axis = 'both', which = 'major', labelsize = 20)

ax.set_xlim((0, 5))
ax.set_ylim((0, 120))
ax.set_aspect(5 / 120)

xmin, xmax = plt.xlim()
ax.axvspan(xmin, xmax, facecolor='lightgray', alpha=0.5)
for time in np.arange(xmin, xmax, 1/7 * 1/24):
    weekend = (int) (time * 7 * 24) % (24 * 7)
    if weekend <= 120:
        ax.axvspan(time, time + 1/24, facecolor="w", alpha=0.5)

ax.set_xticks(np.arange(0, xmax + 1, 1))
ax.set_yticks(np.linspace(0, 100, 3))
ax.tick_params(axis = 'both', which = 'both', pad = 10, width = 3)

for axis in ['bottom','left']:
  ax.spines[axis].set_linewidth(3)
  
for axis in ['top','right']:
  ax.spines[axis].set_linewidth(0)

#fig = plt.gcf()
fig.set_size_inches(6.8, 5, forward = True)
plt.tight_layout()
plt.show()
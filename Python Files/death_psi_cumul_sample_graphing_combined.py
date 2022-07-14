import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style, cm, colors
import matplotlib.gridspec as gridspec

style.use('default')
fig = plt.figure(figsize=(6.8*2, 5))
outer_grid = gridspec.GridSpec(2,4, width_ratios=(2,0.25,1,1), hspace = 0.2)

direct = True
indirect = not direct
plot_width = 3
font = 20
label_font = 30
axis_thickness = 3;

ax = plt.subplot(outer_grid[:,0])
ax.text(-0.275, 1.15, 'A', transform=ax.transAxes,
      color='black', fontweight = 'bold', fontsize = label_font, va='top')

s = '../SPARK Project/Paper Edited Figures/Alpha_PSI_dose.csv'
t = pd.read_csv(s)
s2 = '../SPARK Project/Paper Edited Figures/Delta_PSI_dose.csv'
s2 = 'All_Values.csv'
t2 = pd.read_csv(s2)

if direct:
    s = '../SPARK Project/Paper Edited Figures/Volume_Direct_bottomleft.csv'
    bottomleft = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/Volume_Direct_bottomright.csv'
    bottomright = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/Volume_Direct_topleft.csv'
    topleft = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/Volume_Direct_topright.csv'
    topright = pd.read_csv(s)

if indirect:
    s = '../SPARK Project/Paper Edited Figures/Volume_Indirect_bottomleft.csv'
    bottomleft = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/Volume_Indirect_bottomright.csv'
    bottomright = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/Volume_Indirect_topleft.csv'
    topleft = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/Volume_Indirect_topright.csv'
    topright = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/k_Indirect_bottomleft.csv'
    k_bottomleft = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/k_Indirect_bottomright.csv'
    k_bottomright = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/k_Indirect_topleft.csv'
    k_topleft = pd.read_csv(s)
    s = '../SPARK Project/Paper Edited Figures/k_Indirect_topright.csv'
    k_topright = pd.read_csv(s)

cumul_direct = t.iloc[:,4]
cumul_indirect = t2.iloc[:,4]

if direct:
    alpha = t.iloc[:,1]
    psi_direct = t.iloc[:,3]
    frac_size_direct = t.iloc[:,5]
    num_direct = cumul_direct / frac_size_direct

if indirect:
    psi_indirect = t2.iloc[:,3]
    frac_size_indirect = t2.iloc[:,5]
    num_indirect = cumul_indirect / frac_size_indirect
    delta = t2.iloc[:,2]

if direct:
    ax.set_xlabel(r'$\alpha$' + ' (' + r'Gy$\mathregular{^{-1}}$' + ')', fontsize = font, labelpad = 10)
    ax.set_xlim((0.06, 0.14))
    
if indirect:
    ax.set_xlabel(r'$\delta$', fontsize = font, labelpad = 10)
    ax.set_xlim((0.01, 0.09))

ax.set_ylabel('PSI', fontsize = 20, labelpad = 10)
ax.tick_params(axis = 'both', which = 'major', labelsize = 20)
ax.tick_params(axis = 'both', which = 'both', pad = 10)

ax.set_ylim((0.6, 1))
xmin, xmax = ax.get_xlim()
ymin, ymax = ax.get_ylim()

ax.axvspan(xmin, xmax, facecolor='white', alpha = 0.15)
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

#sc = plt.scatter(x,y, c = c, cmap = cmap, norm = norm, s = 10)
# def density_estimation(m1, m2):
#     X, Y = np.mgrid[xmin:xmax:100j, ymin:ymax:100j]                                                     
#     positions = np.vstack([X.ravel(), Y.ravel()])                                                       
#     values = np.vstack([m1, m2])                                                                        
#     kernel = stats.gaussian_kde(values)                                                                 
#     Z = np.reshape(kernel(positions).T, X.shape)
#     return X, Y, Z

#X, Y, Z = density_estimation(x,y)
#ax.contour(X, Y, Z, 15, linewidths=0.5, colors='k')

# # Number of recursive subdivisions of the initial mesh for smooth plots.
# # Values >3 might result in a very high number of triangles for the refine
# # mesh: new triangles numbering = (4**subdiv)*ntri
# subdiv = 3

# # Float > 0. adjusting the proportion of (invalid) initial triangles which will
# # be masked out. Enter 0 for no mask.
# subdiv = 3
# min_circle_ratio = .01

# tri = Triangulation(x, y)
# ntri = tri.triangles.shape[0]

# mask = TriAnalyzer(tri).get_flat_tri_mask(min_circle_ratio)
# tri.set_mask(mask)

# refiner = UniformTriRefiner(tri)
# tri_refi, z_test_refi = refiner.refine_field(c, subdiv=subdiv)

# z_expected = experiment_res(tri_refi.x, tri_refi.y)

# flat_tri = Triangulation(x_test, y_test)
# flat_tri.set_mask(~mask)

cmap = cm.get_cmap('viridis_r', 10)
cmap2 = cm.get_cmap('viridis_r')
norm=colors.Normalize(min(cumul_direct.min(),cumul_indirect.min()), max(cumul_direct.max(),cumul_indirect.max()))

if indirect:
    y_int = 1.3
    slope = 23
    x1,y1,cumul1 = x[y + slope*x < y_int],y[y + slope*x < y_int],cumul[y + slope*x < y_int]
    sc = ax.tricontourf(x1,y1,cumul1, cmap = cmap, norm = norm)

    y_int = y_int - 0.01
    slope = slope + 0.1
    x2,y2,cumul2 = x[y + slope*x >= y_int],y[y + slope*x >= y_int],cumul[y + slope*x >= y_int]
    sc = ax.scatter(x2,y2,c = cumul2, marker = 's', s = 10, alpha = 1, cmap = cmap, norm = norm)

else:
    sc = ax.scatter(x,y,c = cumul, marker = 's', s = 10, cmap = cmap, norm = norm)

# cols = np.unique(x).shape[0]
# X = x.reshape(-1, cols)
# Y = y.reshape(-1, cols)
# Z = cols.reshape(-1, cols)
# ax.contourf(X,Y,Z)
#x1,y1,cumul1 = x[x < 0.03], y[x < 0.03], cumul[x < 0.03]
# sc = ax.scatter(xg,yg,zg, cmap = cmap, norm = norm)

#sc = ax.tricontourf(x1,y1,cumul1, cmap = cmap, norm = norm)
#sc = ax.scatter(x, y, c = cumul, marker = '.', s = 5, cmap = cmap, norm = norm)

for axis in ['bottom','left']:
  ax.spines[axis].set_linewidth(axis_thickness);
  
for axis in ['top','right']:
  ax.spines[axis].set_linewidth(0);
ax.tick_params(width = axis_thickness);

sm = plt.cm.ScalarMappable(norm=norm, cmap = cmap2)
cb = plt.colorbar(sm, aspect = 30)
cb.ax.set_ylabel('Cumulative Dose (Gy)', labelpad = 15, fontsize = font)
cb.ax.tick_params(axis = 'both', which = 'major', labelsize = font)

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
    text = ax.text(x2[a], y2, labels[a], color='red', fontweight = 'bold', fontsize = label_font if a > 0 else 20)

# [0.005, -0.025]
#plt.scatter(x, y, marker = 'X', c = 'black', s = 400, linewidth = 1.5, edgecolors='black')
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
    #ax2 = ax.twiny()
    weeks = 5 if direct else 7
    scaling = 12 if direct else 11.375
    
    t = read_files[val]
    if indirect:
        k_file = k_files[val]
    
    x = scaling * (t.iloc[:,0])/ 7
    c = 'g'
    ax.plot(x, t.iloc[:,1], linewidth = plot_width, c = c, zorder=2, label = 'V')
    if indirect:
        ax.plot(x, k_file.iloc[:,1], linewidth = plot_width, c = 'darkorange', zorder = 2, label = 'K')
    
    if direct:
        ax.set_xlim((0, weeks * scaling))
        ax.set_ylim((0, 100))
    if indirect:
        ax.set_xlim(0, weeks * scaling)
        ax.set_ylim((0, 145))
    
    ymin, ymax = ax.get_ylim()
    xmin, xmax = ax.get_xlim()
    
    # for time in np.arange(xmin, xmax, 1/7 * 1/24):
    #     weekend = (int) (time * 24 * 7) % (24 * 7)
    #     if weekend < 120:
    #         ax.axvspan(time, time + 1/24, facecolor='white', alpha=0.5)
    
    xrange = [x for x in np.arange(xmin, xmax + 1)]
    yrange = [0, 50, 100]
    new_ticks = [min(xrange), (min(xrange) + max(xrange)) / 2, max(xrange)]
    ax.set_xticks(new_ticks)
    ax.set_yticks(yrange)
    ax.tick_params(axis='both', which='major', width = axis_thickness);
    if (val == 2 or val == 3):
        ax.tick_params(axis='both', which='major', labelsize = font, pad = 5)
        ax.set_xticklabels(int(x) for x in new_ticks)    
    else:
        ax.tick_params(labelbottom=False)
    if (val == 0 or val == 2):
        ax.tick_params(axis='both', which='major', labelsize = font, pad = 5)
        ax.set_yticklabels(int(x) for x in yrange)    
    else:
        ax.tick_params(labelleft=False)
    
    ax.vlines(max(x), 0, ymax, linestyles = 'dotted', color = 'black', linewidth = plot_width, zorder=3)
    ax.hlines((1 - threshold) * 100,0, xmax, colors = 'k', linestyles = 'dotted', 
              color = 'black', linewidth = plot_width, zorder=3)
    
    # ax2.set_xlim([0, xmax])
    # xrange = [int(x) for x in np.arange(xmin, xmax, 1) if (x * 7) % (7) == 0]
    # new_ticks = [min(xrange), (min(xrange) + max(xrange)) / 2, max(xrange)]
    # ax2.set_xticks(new_ticks)
    # ax2.tick_params(axis='both', which='major', width = axis_width)
    # if (val != 2 and val != 3):
    #     ax2.set_xticklabels(str(x * 2 * 5) for x in new_ticks)
    #     ax2.tick_params(axis='both', which='major', labelsize = font, pad = 5)
    # else:
    #     ax2.tick_params(labeltop=False)
    # for axis in ['bottom','left', 'top']:
    #   ax.spines[axis].set_linewidth(axis_width)
        
    for axis in ['bottom','left']:
      ax.spines[axis].set_linewidth(axis_thickness)
      
    for axis in ['right', 'top']:
      ax.spines[axis].set_linewidth(0)
      
    #ax2.spines['top'].set_linewidth(axis_width)
    #ax2.spines['top'].set_linewidth(0)
    #ax2.spines['right'].set_linewidth(0)
    #for key, spine in ax2.spines.items():
        #spine.set_visible(False)
    
    ax.text(0.10, 0.25, labels[val], transform=ax.transAxes,
          color='black', fontweight = 'bold', fontsize = label_font if val > 0 else 20, va='top')
    
    if val == 3 and indirect:
        ax.legend(loc="lower right", fontsize = 12)
    if val == 1:
        if direct:
            ax.text(0.475, 0.85, '-' + r'$\Delta$' + 'V = 32.2%', transform=ax.transAxes,
                color='black', fontweight = 'bold', fontsize = 15, va='top')
            ax.text(0.1, 1.1, 'Required Dose', transform=ax.transAxes, wrap = True,
                color='black', fontweight = 'bold', fontsize = 10, va='top')
        if indirect:
            ax.text(0.475, 0.625, '-' + r'$\Delta$' + 'V = 32.2%', transform=ax.transAxes,
                color='black', fontweight = 'bold', fontsize = 15, va='top')
            ax.text(0.1, 1.1, 'Required Dose', transform=ax.transAxes, wrap = True,
                color='black', fontweight = 'bold', fontsize = 10, va='top')
    if direct:
        if val != 0:
                ax.text(0.6, 0.2, r'$\alpha$' + ' = ' + '{0:.2f}'.format(x2[val]),transform=ax.transAxes,
                        color='black', fontweight = 'bold', fontsize = 15, va='top')
                ax.text(0.5, 0.35, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),transform=ax.transAxes,
                        color='black', fontweight = 'bold', fontsize = 15, va='top')
        else:
            ax.text(0.4, 0.2, r'$\alpha$' + ' = ' + '{0:.2f}'.format(x2[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
            ax.text(0.3, 0.35, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
    if indirect:
        if val == 0:
            ax.text(0.2, 0.825, r'$\delta$' + ' = ' + '{0:.2f}'.format(x2[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
            ax.text(0.1, 0.975, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
        if val == 1:
            ax.text(0.6, 0.20, r'$\delta$' + ' = ' + '{0:.2f}'.format(x2[val]),transform=ax.transAxes,
                color='black', fontweight = 'bold', fontsize = 15, va='top')
            ax.text(0.5, 0.35, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
        if val == 2:
            ax.text(0.4, 0.175, r'$\delta$' + ' = ' + '{0:.2f}'.format(x2[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
            ax.text(0.3, 0.325, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
        if val == 3:
            ax.text(0.6, 0.825, r'$\delta$' + ' = ' + '{0:.2f}'.format(x2[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
            ax.text(0.5, 0.975, 'PSI' + ' = ' + '{0:.2f}'.format(y[val]),transform=ax.transAxes,
                    color='black', fontweight = 'bold', fontsize = 15, va='top')
            
    # if indirect:
    #     ax.text(0.15, 0.25, labels[val], transform=ax.transAxes,
    #       color='black', fontweight = 'bold', fontsize=30, va='top')
    # if direct:
    #     if val < 2:
    #         ax.text(0.15, 0.25, labels[val], transform=ax.transAxes,
    #           color='black', fontweight = 'bold', fontsize=30, va='top')
    #     else:
    #         ax.text(0.85, 0.25, labels[val], transform=ax.transAxes,
    #           color='black', fontweight = 'bold', fontsize=30, va='top')
    
ax = fig.add_subplot(outer_grid[:, 2:])
ax.text(-0.20, 1.15, 'B', transform=ax.transAxes,
      color='black', fontweight = 'bold', fontsize = label_font, va='top')
#ax.set_xlabel('Time on RT (Weeks)', fontsize = font, labelpad = 40)
ax.set_ylabel('% Initial Tumor Volume', fontsize = font, labelpad = 50)
#ax2 = ax.twiny()
ax.set_xlabel('Cumulative Dose (Gy)', fontsize = font, labelpad = 40)
#axs = [ax, ax2]
axs = [ax]
for ax in axs:
    ax.set_xticks([])
    ax.set_yticks([])
    ax.patch.set_alpha(0.01)
    for key, spine in ax.spines.items():
        spine.set_visible(False)

plt.subplots_adjust(top=0.89, bottom=0.195, left=0.09, right=0.985, hspace=0.2, wspace=0.2)
plt.show()
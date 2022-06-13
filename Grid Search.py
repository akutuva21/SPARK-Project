import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy as np

param = pd.read_csv("Grid Search psi_0.9.csv")
coeff = pd.read_csv("Coefficients psi_0.9.csv")

fig = plt.figure()
ax = plt.axes(projection="3d")
#x_points, y_points, z_points, psi, psi_check = param.iloc[:, 0], param.iloc[:, 1], param.iloc[:, 2], param.iloc[:, 3], param.iloc[:, 4]
x_points, y_points, z_points, psi_check = param.iloc[:, 0], param.iloc[:, 1], param.iloc[:, 2], param.iloc[:, 3]
z_points, x_points, y_points, psi_check = map(pd.Series, zip(*sorted(zip(z_points, x_points, y_points, psi_check), reverse=True)))

cmap = plt.get_cmap('bwr', 2)
sc = ax.scatter(x_points, y_points, z_points, c=psi_check, cmap=cmap)
ax.set_xlabel(r'$\lambda$' + ": Lambda", fontsize = 20, labelpad = 20)
ax.set_xlim(0, max(x_points))
ax.set_ylabel(r'$\alpha$' + ": Alpha", fontsize = 20, labelpad = 20)
ax.set_ylim(0, max(y_points))
ax.set_zlabel(r'$\delta$' + ": Delta", fontsize = 20, labelpad = 20)
ax.set_zlim(0, max(z_points))
ax.tick_params(axis='both', which='major', pad = 5)
#cbr = plt.colorbar(sc)
#cbr.set_ticks([0.25, 0.75])
#cbr.set_ticklabels([0, 1])
#cbr.ax.set_ylabel("Something")
red_patch = mpatches.Patch(color='red', label='V(i) / K(i) > 1')
blue_patch = mpatches.Patch(color='blue', label='V(i) / K(i) <= 1')

plt.legend(handles=[red_patch, blue_patch], fontsize = 20)

# =============================================================================
# def equation_plane(x1, y1, z1, x2, y2, z2, x3, y3, z3):
#      
#     a1 = x2 - x1
#     b1 = y2 - y1
#     c1 = z2 - z1
#     a2 = x3 - x1
#     b2 = y3 - y1
#     c2 = z3 - z1
#     a = b1 * c2 - b2 * c1
#     b = a2 * c1 - a1 * c2
#     c = a1 * b2 - b1 * a2
#     d = (- a * x1 - b * y1 - c * z1)
#     #print(a + "x + " + b + "y + " + c + "z = " + d)
#     return [a, b, c, d]
# 
# x1 = x_points[0]
# y1 = y_points[0]
# z1 = z_points[0]
# 
# x2 = x_points[10]
# y2 = y_points[10]
# z2 = x_points[10]
# 
# x3 = x_points[2]
# y3 = y_points[2]
# z3 = x_points[2]
# a,b,c,d = equation_plane(x1, y1, z1, x2, y2, z2, x3, y3, z3)
# =============================================================================

a,b,c,d = coeff.columns.astype(float)
a,b,c,d = 8.099362858520216,-6.799625193655987,35.36042134176889,0.18775305326233238

s = 0.95
x = np.linspace(x_points.min() * s, x_points.max() * 1/s,10)
y = np.linspace(y_points.min() * s, y_points.max() * 1/s,10)

X,Y = np.meshgrid(x,y)
Z = (-d - a*X - b*Y) / c
surf = ax.plot_surface(X, Y, Z)

#plt.title("Cube Search for " + str(len(param.iloc[:, 0])) + " Patients for PSI Boundaries in ARO", fontsize = 30, fontweight = 2)
plt.title("Cube Search for " + str(len(param.iloc[:, 0])) + " Patients for PSI = 0.9", fontsize = 30, fontweight = 2)
plt.show()


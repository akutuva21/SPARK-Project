import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

param = pd.read_csv("Parameter_Search.csv")

fig = plt.figure()
ax = plt.axes(projection="3d")
x_points, y_points, z_points, psi = param.iloc[:, 0], param.iloc[:, 1], param.iloc[:, 2], param.iloc[:, 3]

z_points, x_points, y_points, psi = map(pd.Series, zip(*sorted(zip(z_points, x_points, y_points, psi), reverse=True)))

cmap = plt.get_cmap('winter')
sc = ax.scatter(x_points, y_points, z_points, c=psi, cmap=cmap)
ax.set_xlabel(r'$\lambda$' + ": Lambda", fontsize = 20, labelpad = 20)
#ax.set_xlim(0, max(x_points) * 1.05)
ax.set_ylabel(r'$\alpha$' + ": Alpha", fontsize = 20, labelpad = 20)
#ax.set_ylim(0, max(y_points) * 1.05)
ax.set_zlabel(r'$\delta$' + ": Delta", fontsize = 20, labelpad = 20)
#ax.set_zlim(0, max(z_points) * 1.05)
ax.tick_params(axis='both', which='major', pad = 5)
cbr = plt.colorbar(sc)
#cbr.set_ticks([0.25, 0.75])
#cbr.set_ticklabels([0, 1])
cbr.ax.set_ylabel("Initial PSI")

# =============================================================================
# def equation_plane(x1, y1, z1, x2, y2, z2, x3, y3, z3):
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
#     return [a, b, c, d]
# 
# x1 = x_points[0]
# y1 = y_points[0]
# z1 = z_points[0]
# 
# x2 = x_points[100]
# y2 = y_points[100]
# z2 = z_points[100]
# 
# x3 = x_points[500]
# y3 = y_points[500]
# z3 = z_points[500]
# 
# a,b,c,d = equation_plane(x1, y1, z1, x2, y2, z2, x3, y3, z3)
# print(a, b, c, d)
# =============================================================================

a,b,c,d = 1.0136983018350241E-4,1.0259428432748304E-4,-6.876666649997332E-5,-6.3768463291947866E-6

x = np.linspace(x_points.min(),x_points.max(),10)
y = np.linspace(y_points.min(),y_points.max(),10)

X,Y = np.meshgrid(x,y)
Z = (-d - a*X - b*Y) / c
surf = ax.plot_surface(X, Y, Z)

plt.title("Cube Search for " + str(len(param.iloc[:, 0])) + " Patients for PSI Boundaries in ARO, V(i) / K(i) <= 1", fontsize = 30, fontweight = 2)
plt.show()


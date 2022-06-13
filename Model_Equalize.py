# -*- coding: utf-8 -*-
"""
Created on Wed Jul 14 14:54:22 2021

@author: coola
"""
import random, math
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

k0 = 100
lam = 1
t = 0.1
v0 = 80
r = random.seed(7)
i = 0
x, y, z = list(), list(), list()
dose_list = list(np.linspace(0.1, 20, 200))


while i != 5000:
    delta = random.random()
    dose = random.choice(dose_list)
    alpha = random.random()
    gamma = 1 - math.exp(-alpha * dose - alpha/10 * dose**2)
    y1 = (1 - delta) / (1 + ((k0 * (1 - delta) / v0) - 1) * math.exp(-t))
    y2 = 1 / (1 + (k0 / (v0 - v0 * gamma * (1 - v0/k0)) - 1) * math.exp(t))
    
    #print(y1, y2)
    if math.isclose(y1, y2, abs_tol=1e-6):
        x.append(delta)
        y.append(dose)
        z.append(alpha)
        i += 1
        print(i)

pd.Series(x, y, z).reset_index().to_csv('delta_vs_dose_vs_alpha.csv', header = ['Dose', 'Delta', 'Alpha'], index = False)
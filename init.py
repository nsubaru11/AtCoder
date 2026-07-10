import sys, math, itertools as it
from itertools import permutations, combinations, combinations_with_replacement
from itertools import product, accumulate, groupby, chain, count, cycle, pairwise
from collections import deque, defaultdict, Counter
from functools import lru_cache, reduce
from fractions import Fraction
from decimal import Decimal, getcontext
import heapq, bisect, random

input = sys.stdin.readline
sys.setrecursionlimit(10**7)
getcontext().prec = 50

def ii():  return int(input())
def mi():  return map(int, input().split())
def li():  return list(map(int, input().split()))
def ls():  return input().split()

def show(x): print(list(x))
def grids(g): print('\n'.join(' '.join(map(str, r)) for r in g))
def grid(g): print('\n'.join(''.join(map(str, r)) for r in g))
INF = float('inf')

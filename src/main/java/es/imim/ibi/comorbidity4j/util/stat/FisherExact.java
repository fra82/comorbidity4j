// ContigencyTable.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package es.imim.ibi.comorbidity4j.util.stat;

/*
 * SNVer: a statistical tool for calling common and rare variants
 * in analysis of pool or individual next-generation sequencing data.
 *
 * Copyright (C) 2011  New Jersey Institute of technology
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * FROM: http://snver.sourceforge.net/
 * SOURCE: http://svn.code.sf.net/p/snver/code/trunk/svn/src/net/sf/snver/pileup/util/math/FisherExact.java
 * 
 */

/**
 * This does a Fisher Exact test.  The Fisher's Exact test procedure calculates an exact probability value
 * for the relationship between two dichotomous variables, as found in a two by two crosstable. The program
 * calculates the difference between the data observed and the data expected, considering the given marginal
 * and the assumptions of the model of independence. It works in exactly the same way as the Chi-square test
 * for independence; however, the Chi-square gives only an estimate of the true probability value, an estimate
 * which might not be very accurate if the marginal is very uneven or if there is a small value (less than five)
 * in one of the cells.
 * <p/>
 * It uses an array of factorials initialized at the beginning to provide speed.
 * There could be better ways to do this.
 *
 * @author Ed Buckler
 * @version $Id: FisherExact.java,v 1
 */

public class FisherExact {
	private static final boolean DEBUG = false;
	private static double[] fprecomputed = new double[100000];
	private double[] f;
	int maxSize;

	static {
		fprecomputed[0] = 0.0;
		for (int i = 1; i < 100000; i++) {
			fprecomputed[i] = fprecomputed[i - 1] + Math.log(i);
		}
	}


	/**
	 * constructor for FisherExact table
	 *
	 * @param maxSize is the maximum sum that will be encountered by the table (a+b+c+d)
	 */
	public FisherExact(int maxSize) {
		this.maxSize = maxSize;
		double cf = 1.0;
		f = new double[maxSize + 1];
		f[0] = 0.0;
		for (int i = 1; i <= this.maxSize; i++) {
			if(i < fprecomputed.length) {
				f[i] = fprecomputed[i];
			}
			else {
				f[i] = f[i - 1] + Math.log(i);
			}
		}
	}

	public FisherExact(int maxSize, boolean useLookup) {
		this.maxSize = maxSize;
		double cf = 1.0;
		f = new double[maxSize + 1];
		f[0] = 0.0;
		for (int i = 1; i <= this.maxSize; i++) {
			f[i] = f[i - 1] + Math.log(i);
		}
		int count = 0;
		double minP = 0.05;
		for (int i = 1; i < maxSize; i++) {
			for (int j = 1; j < maxSize; j++) {
				for (int k = 1; k < maxSize; k++) {
					for (int m = 1; m < maxSize; m++) {
						if (getTwoTailedP(i, j, k, m) < minP) {
							count++;
						}
					}
				}
			}

		}
		System.out.printf("MaxSize %d minP: %g  Count: %d %n", maxSize, minP, count);
	}

	/**
	 * calculates the P-value for this specific state
	 *
	 * @param a a, b, c, d are the four cells in a 2x2 matrix
	 * @param b
	 * @param c
	 * @param d
	 * @return the P-value
	 */
	public final double getP(int a, int b, int c, int d) {
		int n = a + b + c + d;
		if (n > maxSize) {
			return Double.NaN;
		}
		double p;
		p = (f[a + b] + f[c + d] + f[a + c] + f[b + d]) - (f[a] + f[b] + f[c] + f[d] + f[n]);
		return Math.exp(p);
	}

	/**
	 * Calculates the one-tail P-value for the Fisher Exact test.  Determines whether to calculate the right- or left-
	 * tail, thereby always returning the smallest p-value.
	 *
	 * @param a a, b, c, d are the four cells in a 2x2 matrix
	 * @param b
	 * @param c
	 * @param d
	 * @return one-tailed P-value (right or left, whichever is smallest)
	 */
	public final double getCumlativeP(int a, int b, int c, int d) {
		int min, i;
		int n = a + b + c + d;
		if (n > maxSize) {
			return Double.NaN;
		}
		double p = 0;

		p += getP(a, b, c, d);
		if (DEBUG) {
			System.out.println("p = " + p);
		}
		if ((a * d) >= (b * c)) {
			if (DEBUG) {
				System.out.println("doing R-tail: a=" + a + " b=" + b + " c=" + c + " d=" + d);
			}
			min = (c < b) ? c : b;
			for (i = 0; i < min; i++) {
				if (DEBUG) {
					System.out.print("doing round " + i);
				}
				p += getP(++a, --b, --c, ++d);
				if (DEBUG) {
					System.out.println("\ta=" + a + " b=" + b + " c=" + c + " d=" + d);
				}
			}
			//           System.out.println("");
		}
		if ((a * d) < (b * c)) {
			if (DEBUG) {
				System.out.println("doing L-tail: a=" + a + " b=" + b + " c=" + c + " d=" + d);
			}
			min = (a < d) ? a : d;
			for (i = 0; i < min; i++) {
				if (DEBUG) {
					System.out.print("doing round " + i);
				}
				double pTemp = getP(--a, ++b, ++c, --d);
				if (DEBUG) {
					System.out.print("\tpTemp = " + pTemp);
				}
				p += pTemp;
				if (DEBUG) {
					System.out.println("\ta=" + a + " b=" + b + " c=" + c + " d=" + d);
				}
			}
		}
		return p;
	}

	/**
	 * Calculates the right-tail P-value for the Fisher Exact test.
	 *
	 * @param a a, b, c, d are the four cells in a 2x2 matrix
	 * @param b
	 * @param c
	 * @param d
	 * @return one-tailed P-value (right-tail)
	 */
	public final double getRightTailedP(int a, int b, int c, int d) {
		int min, i;
		int n = a + b + c + d;
		if (n > maxSize) {
			return Double.NaN;
		}
		double p = 0;

		p += getP(a, b, c, d);
		if (DEBUG) {
			System.out.println("p = " + p);
		}
		if (DEBUG) {
			System.out.println("doing R-tail: a=" + a + " b=" + b + " c=" + c + " d=" + d);
		}
		min = (c < b) ? c : b;
		for (i = 0; i < min; i++) {
			p += getP(++a, --b, --c, ++d);

		}
		return p;
	}

	/**
	 * Calculates the right-tail P-value for the Fisher Exact test.
	 *
	 * @param a a, b, c, d are the four cells in a 2x2 matrix
	 * @param b
	 * @param c
	 * @param d
	 * @return one-tailed P-value (right-tail)
	 */
	public final double getRightTailedPQuick(int a, int b, int c, int d, double maxP) {
		int min, i;
		double p = 0;

		p += getP(a, b, c, d);
		min = (c < b) ? c : b;
		for (i = 0; (i < min) && (p < maxP); i++) {
			p += getP(++a, --b, --c, ++d);

		}
		return p;
	}

	/**
	 * Calculates the left-tail P-value for the Fisher Exact test.
	 *
	 * @param a a, b, c, d are the four cells in a 2x2 matrix
	 * @param b
	 * @param c
	 * @param d
	 * @return one-tailed P-value (left-tail)
	 */
	public final double getLeftTailedP(int a, int b, int c, int d) {
		int min, i;
		int n = a + b + c + d;
		if (n > maxSize) {
			return Double.NaN;
		}
		double p = 0;

		p += getP(a, b, c, d);
		if (DEBUG) {
			System.out.println("p = " + p);
		}
		if (DEBUG) {
			System.out.println("doing L-tail: a=" + a + " b=" + b + " c=" + c + " d=" + d);
		}
		min = (a < d) ? a : d;
		for (i = 0; i < min; i++) {
			if (DEBUG) {
				System.out.print("doing round " + i);
			}
			double pTemp = getP(--a, ++b, ++c, --d);
			if (DEBUG) {
				System.out.print("\tpTemp = " + pTemp);
			}
			p += pTemp;
			if (DEBUG) {
				System.out.println("\ta=" + a + " b=" + b + " c=" + c + " d=" + d);
			}
		}


		return p;
	}

	/**
	 * Calculates the two-tailed P-value for the Fisher Exact test.
	 * <p/>
	 * In order for a table under consideration to have its p-value included
	 * in the final result, it must have a p-value less than the original table's P-value, i.e.
	 * Fisher's exact test computes the probability, given the observed marginal
	 * frequencies, of obtaining exactly the frequencies observed and any configuration more extreme.
	 * By "more extreme," we mean any configuration (given observed marginals) with a smaller probability of
	 * occurrence in the same direction (one-tailed) or in both directions (two-tailed).
	 *
	 * @param a a, b, c, d are the four cells in a 2x2 matrix
	 * @param b
	 * @param c
	 * @param d
	 * @return two-tailed P-value
	 */
	public final double getTwoTailedP(int a, int b, int c, int d) {
		int min, i;
		int n = a + b + c + d;
		if (n > maxSize) {
			return Double.NaN;
		}
		double p = 0;

		double baseP = getP(a, b, c, d);
		//         in order for a table under consideration to have its p-value included
		//         in the final result, it must have a p-value less than the baseP, i.e.
		//         Fisher's exact test computes the probability, given the observed marginal
		//         frequencies, of obtaining exactly the frequencies observed and any configuration more extreme.
		//         By "more extreme," we mean any configuration (given observed marginals) with a smaller probability of
		//         occurrence in the same direction (one-tailed) or in both directions (two-tailed).

		if (DEBUG) {
			System.out.println("baseP = " + baseP);
		}
		int initialA = a, initialB = b, initialC = c, initialD = d;
		p += baseP;
		if (DEBUG) {
			System.out.println("p = " + p);
		}
		if (DEBUG) {
			System.out.println("Starting with R-tail: a=" + a + " b=" + b + " c=" + c + " d=" + d);
		}
		min = (c < b) ? c : b;
		for (i = 0; i < min; i++) {
			if (DEBUG) {
				System.out.print("doing round " + i);
			}
			double tempP = getP(++a, --b, --c, ++d);
			if (tempP <= baseP) {
				if (DEBUG) {
					System.out.print("\ttempP (" + tempP + ") is less than baseP (" + baseP + ")");
				}
				p += tempP;
			}
			if (DEBUG) {
				System.out.println(" a=" + a + " b=" + b + " c=" + c + " d=" + d);
			}
		}

		// reset the values to their original so we can repeat this process for the other side
		a = initialA;
		b = initialB;
		c = initialC;
		d = initialD;

		if (DEBUG) {
			System.out.println("Now doing L-tail: a=" + a + " b=" + b + " c=" + c + " d=" + d);
		}
		min = (a < d) ? a : d;
		if (DEBUG) {
			System.out.println("min = " + min);
		}
		for (i = 0; i < min; i++) {
			if (DEBUG) {
				System.out.print("doing round " + i);
			}
			double pTemp = getP(--a, ++b, ++c, --d);
			if (DEBUG) {
				System.out.println("  pTemp = " + pTemp);
			}
			if (pTemp <= baseP) {
				if (DEBUG) {
					System.out.print("\ttempP (" + pTemp + ") is less than baseP (" + baseP + ")");
				}
				p += pTemp;
			}
			if (DEBUG) {
				System.out.println(" a=" + a + " b=" + b + " c=" + c + " d=" + d);
			}
		}
		return p;
	}

	public static void main(String[] args) {
		int a = 1;
		int b = 1;
		int c = 0;
		int d = 50;
		FisherExact fisherExact = new FisherExact(a + b + c + d + 10);
		double twoTailedP = fisherExact.getTwoTailedP(a, b, c, d);
		System.out.println(twoTailedP);

		double tmp = 0;
		System.out.println((-1) * 10 * Math.log10(tmp));
	}
}

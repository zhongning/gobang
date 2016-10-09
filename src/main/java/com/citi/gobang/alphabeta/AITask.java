package com.citi.gobang.alphabeta;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class AITask implements Callable<List<Point>> {
	int[][] isChessOn;
	boolean able_flag;
	int deep = 8;
	int weight = 10;
	int sbw;
	int bwf;
	protected int x_max = 15, x_min = 0;
	protected int y_max = 15, y_min = 0;
	List<Point> bestPoints;

	public AITask(int[][] isChessOn, boolean able_flag, int deep, int weight, int sbw, int bwf, int x_max, int x_min,
			int y_max, int y_min, List<Point> bestPoints) {
		this.isChessOn = isChessOn;
		this.able_flag = able_flag;
		this.deep = deep;
		this.weight = weight;
		this.sbw = sbw;
		this.bwf = bwf;
		this.x_max = x_max;
		this.x_min = x_min;
		this.y_max = y_max;
		this.y_min = y_min;
		this.bestPoints = bestPoints;
	}

	@Override
	public List<Point> call() throws Exception {
		int x, y, mx = -100000000;
		x = y = -1;
		// 搜索最优下棋点
		int[][] bests = getBests(bwf);
		for (int k = 0; k < bests.length; k++) {
			int i = bests[k][0];
			int j = bests[k][1];
			// 有成5,则直接下子,并退出循环..没有,则思考对方情况
			if (getType(i, j, bwf) == 1) {
				x = i;
				y = j;
				bestPoints.add(new Point(x,y));
				return bestPoints;
			}
			if (getType(i, j, 1 - bwf) == 1) {
				x = i;
				y = j;
				bestPoints.add(new Point(x,y));
				return bestPoints;
			}
		}
		for (int k = 0; k < bests.length; k++) {
			System.out.println("getBests: "+k);
			int i = bests[k][0];
			int j = bests[k][1];
			if(bestPoints.isEmpty()){
				bestPoints.add(new Point(i,j));
			}
			// 预存当前边界值
			int temp1 = x_min, temp2 = x_max, temp3 = y_min, temp4 = y_max;
			// 预设己方下棋,并更新边界值
			isChessOn[i][j] = bwf;
			resetMaxMin(i, j);
			// 预测未来
			int t = findMin(-100000000, 100000000, deep - 1);
			// 还原预设下棋位置以及边界值
			isChessOn[i][j] = 2;
			x_min = temp1;
			x_max = temp2;
			y_min = temp3;
			y_max = temp4;
			// 差距小于1000，50%概率随机选取
			// System.out.println("外 :" + i + "," + j + " mx:" + mx + " t:" +
			// t);
			if (t - mx > 1000 || Math.abs(t - mx) < 1000 && randomTest(2)) {
				x = i;
				y = j;
				mx = t;
				bestPoints.clear();
				bestPoints.add(new Point(x,y));
				// System.out.println(i + "," + j + " mx:" + mx + " t:" + t);
			}

		}
		return bestPoints;
	}

	// --------------------------对当前棋面进行打分------------------------------------------------------------//

	protected int evaluate() {
		int rt = 0, mt_c = 1, mt_m = 1;
		if (bwf == sbw)
			mt_m = 2;
		else
			mt_c = 2;
		int i_min = (x_min == 0 ? x_min : x_min - 1);
		int j_min = (y_min == 0 ? y_min : y_min - 1);
		int i_max = (x_max == 15 ? x_max : x_max + 1);
		int j_max = (y_max == 15 ? y_max : y_max + 1);
		for (int i = i_min; i < i_max; i++)
			for (int j = j_min; j < j_max; j++)
				if (isChessOn[i][j] == 2) {
					// 电脑棋面分数
					int type = getType(i, j, 1 - sbw);
					if (type == 1) // 棋型1,棋型2以及棋型3,加权.
									// 防止"4个双活3"的局分大于"1个双四"之类的错误出现
						rt += 30 * mt_c * getMark(type);

					else if (type == 2)
						rt += 10 * mt_c * getMark(type);
					else if (type == 3)
						rt += 3 * mt_c * getMark(type);
					else
						rt += mt_c * getMark(type);
					// 玩家棋面分数
					type = getType(i, j, sbw);
					if (type == 1)
						rt -= 30 * mt_m * getMark(type);
					else if (type == 2)
						rt -= 10 * mt_m * getMark(type);
					else if (type == 3)
						rt -= 3 * mt_m * getMark(type);
					else
						rt -= mt_m * getMark(type);
				}
		return rt;
	}

	private int[][] getBests(int bwf) {

		int i_min = (x_min == 0 ? x_min : x_min - 1);
		int j_min = (y_min == 0 ? y_min : y_min - 1);
		int i_max = (x_max == 15 ? x_max : x_max + 1);
		int j_max = (y_max == 15 ? y_max : y_max + 1);
		int n = 0;
		int type_1, type_2;
		int[][] rt = new int[(i_max - i_min) * (j_max - j_min)][3];
		for (int i = i_min; i < i_max; i++)
			for (int j = j_min; j < j_max; j++)
				if (isChessOn[i][j] == 2) {
					if (Thread.currentThread().isInterrupted()) {
						return null;
					}
					type_1 = getType(i, j, bwf);
					if (type_1 == 1) {
						return new int[][] { { i, j } };
					}
					type_2 = getType(i, j, 1 - bwf);
					if (able_flag && bwf == 0 && (type_1 == 20 || type_1 == 21 || type_1 == 22)) // 禁手棋位置,不记录
						continue;
					rt[n][0] = i;
					rt[n][1] = j;
					rt[n][2] = getMark(type_1) + getMark(type_2);
					n++;
				}
		// 对二维数组排序
		Arrays.sort(rt, new ArrComparator());
		int size = weight > n ? n : weight;
		int[][] bests = new int[size][3];
		System.arraycopy(rt, 0, bests, 0, size);
		return bests;
	}

	// ----------------------------计算指定方位上的棋型-------------------//
	// x,y 方向线基准一点。
	// ex,ey 指定方向步进向量。
	// k 棋子颜色，0：黑色，1：白色
	// 该方向上的棋子数目 以及 活度
	private int[] count(int x, int y, int ex, int ey, int bwf) {
		// 该方向没意义,返回0
		if (!makesense(x, y, ex, ey, bwf))// makesense()为判断是否大于5
			return new int[] { 0, 1 };

		// 正方向 以及 反方向棋子个数
		int rt_1 = 1, rt_2 = 1;
		// 总棋子个数
		int rt = 1;
		// 正方向 以及 反方向连子的活度
		int ok_1 = 0, ok_2 = 0;
		// 总活度
		int ok = 0;
		// 连子中间有无空格
		boolean flag_mid1 = false, flag_mid2 = false;
		// 连子中间空格的位置
		int flag_i1 = 1, flag_i2 = 1;

		if (isChessOn[x][y] != 2) {
			throw new IllegalArgumentException("position x,y must be empty!..");
		}
		int i;
		// 往正方向搜索
		for (i = 1; x + i * ex < 15 && x + i * ex >= 0 && y + i * ey < 15 && y + i * ey >= 0; i++) {
			if (isChessOn[x + i * ex][y + i * ey] == bwf)
				rt_1++;
			// 位置为空,若中空标志为false,则记为中空并继续搜索 否则,break
			else if (isChessOn[x + i * ex][y + i * ey] == 2) {
				if (!flag_mid1) {
					flag_mid1 = true;
					flag_i1 = i;
				} else
					break;
			}
			// 位置为对方棋子
			else
				break;
		}
		// 计算正方向活度,,
		// 最后一个位置不超过边界
		if (x + i * ex < 15 && x + i * ex >= 0 && y + i * ey < 15 && y + i * ey >= 0) {
			// 最后一个位置为空位 +1活
			if (isChessOn[x + i * ex][y + i * ey] == 2) {
				ok_1++;
				// 若是在尾部检测到连续的空格而退出搜索,则不算有中空
				if (rt_1 == flag_i1)
					flag_mid1 = false;
				// 若中空的位置在4以下 且 棋子数>=4,则这一边的4非活
				if (flag_mid1 && rt_1 > 3 && flag_i1 < 4) {
					ok_1--;
				}
			}
			// 最后一个位置不是空格,且搜索了2步以上,若前一个是空格, 则不算中空,且为活的边
			else if (isChessOn[x + i * ex][y + i * ey] != bwf && i >= 2)
				if (isChessOn[x + (i - 1) * ex][y + (i - 1) * ey] == 2) {
					ok_1++;
					flag_mid1 = false;
				}
		}
		// 最后一个位置是边界 搜索了2步以上,且前一个是空格, 则不算中空,且为活的边
		else if (i >= 2 && isChessOn[x + (i - 1) * ex][y + (i - 1) * ey] == 2) {
			ok_1++;
			flag_mid1 = false;
		}

		// 往反方向搜索
		for (i = 1; x - i * ex >= 0 && x - i * ex < 15 && y - i * ey >= 0 && y - i * ey < 15; i++) {
			if (isChessOn[x - i * ex][y - i * ey] == bwf)
				rt_2++;
			else if (isChessOn[x - i * ex][y - i * ey] == 2) {
				if (!flag_mid2) {
					flag_mid2 = true;
					flag_i2 = i;
				} else
					break;
			} else
				break;
		}
		// 计算反方向活度
		if (x - i * ex < 15 && x - i * ex >= 0 && y - i * ey < 15 && y - i * ey >= 0) {
			if (isChessOn[x - i * ex][y - i * ey] == 2) {
				ok_2++;
				if (rt_2 == flag_i2)
					flag_mid2 = false;
				if (flag_mid2 && rt_2 > 3 && flag_i2 < 4) {
					ok_2--;
				}
			} else if (isChessOn[x - i * ex][y - i * ey] != bwf && i >= 2)
				if (isChessOn[x - (i - 1) * ex][y - (i - 1) * ey] == 2) {
					ok_2++;
					flag_mid2 = false;
				}
		} else if (i >= 2 && isChessOn[x - (i - 1) * ex][y - (i - 1) * ey] == 2) {
			ok_2++;
			flag_mid2 = false;
		}

		// ------------------分析棋子类型
		// 两边都没中空,直接合成
		if (!flag_mid1 && !flag_mid2) {
			rt = rt_1 + rt_2 - 1;
			ok = ok_1 + ok_2;
			return new int[] { rt, ok };
		}
		// 两边都有中空
		else if (flag_mid1 && flag_mid2) {
			int temp = flag_i1 + flag_i2 - 1;
			// 判断中间的纯连子数,在5以上,直接返回; 为4,返回活4;
			if (temp >= 5)
				return new int[] { temp, 2 };
			if (temp == 4)
				return new int[] { temp, 2 };
			// 先看有没死4,再看有没活3,剩下只能是死3
			if (rt_1 + flag_i2 - 1 >= 4 || rt_2 + flag_i1 - 1 >= 4)
				return new int[] { 4, 1 };
			if (rt_1 + flag_i2 - 1 == 3 && ok_1 > 0 || rt_2 + flag_i1 - 1 == 3 && ok_2 > 0)
				return new int[] { 3, 2 };
			return new int[] { 3, 1 };
		}
		// 有一边有中空
		else {
			// 总棋子数少于5,直接合成
			if (rt_1 + rt_2 - 1 < 5)
				return new int[] { rt_1 + rt_2 - 1, ok_1 + ok_2 };
			// 多于5,先找成5,再找活4,剩下的只能是死4
			else {
				if (flag_mid1 && rt_2 + flag_i1 - 1 >= 5)
					return new int[] { rt_2 + flag_i1 - 1, ok_2 + 1 };
				if (flag_mid2 && rt_1 + flag_i2 - 1 >= 5)
					return new int[] { rt_1 + flag_i2 - 1, ok_1 + 1 };

				if (flag_mid1 && (rt_2 + flag_i1 - 1 == 4 && ok_2 == 1 || flag_i1 == 4))
					return new int[] { 4, 2 };
				if (flag_mid2 && (rt_1 + flag_i2 - 1 == 4 && ok_1 == 1 || flag_i2 == 4))
					return new int[] { 4, 2 };

				return new int[] { 4, 1 };
			}
		}
	}

	// ----------------------------判断指定方向下棋是否有意义,即最大可能的棋子数是否
	// >=5-------------------------------//
	// x,y 评估的基准点
	// ex,ey 方向向量
	// k 棋色
	// true:有意义 false:没意义
	private Boolean makesense(int x, int y, int ex, int ey, int bwf) {

		int rt = 1;
		for (int i = 1; x + i * ex < 15 && x + i * ex >= 0 && y + i * ey < 15 && y + i * ey >= 0 && rt < 5; i++)
			if (isChessOn[x + i * ex][y + i * ey] != 1 - bwf)
				rt++;
			else
				break;

		for (int i = 1; x - i * ex >= 0 && x - i * ex < 15 && y - i * ey >= 0 && y - i * ey < 15 && rt < 5; i++)
			if (isChessOn[x - i * ex][y - i * ey] != 1 - bwf)
				rt++;
			else
				break;
		return (rt >= 5);
	}

	// ------------------------------------
	// 棋型判别-------------------------------------//
	// x,y 落子位置
	// bwf 棋色 0：黑子，1：白子
	// 对应的棋型： 棋型代码对应如下：
	// 1：成5
	// 2：成活4或者是双死4或者是死4活3
	// 3：成双活3
	// 4：成死3活3
	// 5：成死4
	// 6：单活3
	// 7：成双活2
	// 8：成死3
	// 9：成死2活2
	// 10：成活2
	// 11：成死2
	// 12: 其他
	// 20: 长连禁手
	// 21: 双四禁手
	// 22: 双活三禁手

	protected int getType(int x, int y, int bwf) {
		if (isChessOn[x][y] != 2)
			return -1;
		int[][] types = new int[4][2];
		types[0] = count(x, y, 0, 1, bwf); // 竖直
		types[1] = count(x, y, 1, 0, bwf); // 横向
		types[2] = count(x, y, -1, 1, bwf); // 斜上
		types[3] = count(x, y, 1, 1, bwf); // 斜下
		// 各种棋型的方向的数目
		int longfive = 0;
		int five_OR_more = 0;
		int four_died = 0, four_live = 0;
		int three_died = 0, three_live = 0;
		int two_died = 0, two_live = 0;
		// 各方向上棋型的判别
		for (int k = 0; k < 4; k++) {
			if (types[k][0] > 5) {
				longfive++; // 长连
				five_OR_more++;
			} else if (types[k][0] == 5)
				five_OR_more++; // 成5
			else if (types[k][0] == 4 && types[k][1] == 2)
				four_live++; // 活4
			else if (types[k][0] == 4 && types[k][1] != 2)
				four_died++; // 死4
			else if (types[k][0] == 3 && types[k][1] == 2)
				three_live++; // 活3
			else if (types[k][0] == 3 && types[k][1] != 2)
				three_died++; // 死3
			else if (types[k][0] == 2 && types[k][1] == 2)
				two_live++; // 活2
			else if (types[k][0] == 2 && types[k][1] != 2)
				two_died++; // 死2
			else
				;
		}
		// 总棋型的判别
		if (bwf == 0 && able_flag) { // 黑棋且选择有禁手
			if (longfive != 0) // 长连禁手
				return 20;
			if (four_live + four_died >= 2) // 双4禁手
				return 21;
			if (three_live >= 2) // 双活三禁手
				return 22;
		}
		if (five_OR_more != 0)
			return 1; // 成5
		if (four_live != 0 || four_died >= 2 || four_died != 0 && three_live != 0)
			return 2; // 成活4或者是双死4或者是死4活3
		if (three_live >= 2)
			return 3; // 成双活3
		if (three_died != 0 && three_live != 0)
			return 4; // 成死3活3
		if (four_died != 0)
			return 5; // 成死4
		if (three_live != 0)
			return 6; // 单活3
		if (two_live >= 2)
			return 7; // 成双活2
		if (three_died != 0)
			return 8; // 成死3
		if (two_live != 0 && two_died != 0)
			return 9; // 成死2活2
		if (two_live != 0)
			return 10; // 成活2
		if (two_died != 0)
			return 11; // 成死2
		return 12;
	}

	public void resetMaxMin(int x, int y) {
		if (x - 1 >= 0)
			x_min = (x_min < x - 1 ? x_min : x - 1);
		if (x + 1 <= 15)
			x_max = (x_max > x + 1 ? x_max : x + 1);
		if (y - 1 >= 0)
			y_min = (y_min < y - 1 ? y_min : y - 1);
		if (y + 1 <= 15)
			y_max = (y_max > y + 1 ? y_max : y + 1);

	}

	// ---------搜索当前搜索状态极大值--------------------------------//
	// alpha 祖先节点得到的当前最小最大值，用于alpha 剪枝
	// beta 祖先节点得到的当前最大最小值，用于beta 剪枝。
	// step 还要搜索的步数
	// return 当前搜索子树极大值
	protected int findMax(int alpha, int beta, int step) {
		int max = alpha;
		if (step == 0) {
			return evaluate();
		}
		int[][] rt = getBests(1 - sbw);
		for (int i = 0; i < rt.length; i++) {
			int x = rt[i][0];
			int y = rt[i][1];
			if (getType(x, y, 1 - sbw) == 1) // 电脑可取胜
				return 100 * (getMark(1) + step * 1000);
			isChessOn[x][y] = 1 - sbw;
			// 预存当前边界值
			int temp1 = x_min, temp2 = x_max, temp3 = y_min, temp4 = y_max;
			resetMaxMin(x, y);
			int t = findMin(max, beta, step - 1);
			isChessOn[x][y] = 2;
			// 还原预设边界值
			x_min = temp1;
			x_max = temp2;
			y_min = temp3;
			y_max = temp4;
			if (t > max)
				max = t;
			// beta 剪枝
			if (max >= beta)
				return max;
		}
		return max;
	}

	// -----------------------搜索当前搜索状态极小值---------------------------------//
	// alpha 祖先节点得到的当前最小最大值，用于alpha 剪枝
	// beta 祖先节点得到的当前最大最小值，用于beta 剪枝
	// step 还要搜索的步数
	// return 当前搜索子树极小值。
	protected int findMin(int alpha, int beta, int step) {
		int min = beta;
		if (step == 0) {
			return evaluate();
		}
		int[][] rt = getBests(sbw);
		for (int i = 0; i < rt.length; i++) {
			int x = rt[i][0];
			int y = rt[i][1];
			int type = getType(x, y, sbw);
			if (type == 1) // 玩家成5
				return -100 * (getMark(1) + step * 1000);
			// 预存当前边界值
			int temp1 = x_min, temp2 = x_max, temp3 = y_min, temp4 = y_max;
			isChessOn[x][y] = sbw;
			resetMaxMin(x, y);
			int t = findMax(alpha, min, step - 1);
			isChessOn[x][y] = 2;
			// 还原预设边界值
			x_min = temp1;
			x_max = temp2;
			y_min = temp3;
			y_max = temp4;
			if (t < min)
				min = t;
			// alpha 剪枝
			if (min <= alpha) {
				return min;
			}
		}
		return min;
	}

	// -------------------------------------
	// 不同棋型对应分数---------------------------------
	// k 棋型代号
	// return 对应分数
	private int getMark(int k) {
		switch (k) {
		case 1:
			return 100000;
		case 2:
			return 30000;
		case 3:
			return 5000;
		case 4:
			return 1000;
		case 5:
			return 500;
		case 6:
			return 200;
		case 7:
			return 100;
		case 8:
			return 50;
		case 9:
			return 10;
		case 10:
			return 5;
		case 11:
			return 3;
		case 12:
			return 2;
		default: // 禁手棋型
			return 0;
		}
	}

	// ------------------------------------------对分数相同的落子点，随机选取-------------------//
	// kt 随机因子 值越小，被选取的概率越大
	// return 是否选择该位置

	private boolean randomTest(int kt) {
		Random rm = new Random();
		return rm.nextInt() % kt == 0;
	}
}

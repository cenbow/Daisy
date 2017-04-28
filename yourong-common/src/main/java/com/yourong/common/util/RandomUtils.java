package com.yourong.common.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rop.thirdparty.com.google.common.collect.Lists;

import com.google.common.collect.Sets;

/**
 * 随机数工具类
 *
 */
public class RandomUtils {

	private static Logger logger = LoggerFactory.getLogger(RandomUtils.class);

	/**
	 * 1点出现的概率为%50
	 */
	public static double rateForDick1 = 0.40;
	/**
	 * 2点出现的概率为%30
	 */
	public static double rateForDick2 = 0.40;
	/**
	 * 3点出现的概率为%10
	 */
	public static double rateForDick3 = 0.15;
	/**
	 * 4点出现的概率为%5
	 */
	public static double rateForDick4 = 0.03;
	/**
	 * 5点出现的概率为%5
	 */
	public static double rateForDick5 = 0.01;
	/**
	 * 6点出现的概率为%2
	 */
	public static double rateForDick6 = 0.01;

	public static double getRateForDick1() {
		return rateForDick1;
	}

	public static void setRateForDick1(double rateForDick1) {
		RandomUtils.rateForDick1 = rateForDick1;
	}

	public static double getRateForDick2() {
		return rateForDick2;
	}

	public static void setRateForDick2(double rateForDick2) {
		RandomUtils.rateForDick2 = rateForDick2;
	}

	public static double getRateForDick3() {
		return rateForDick3;
	}

	public static void setRateForDick3(double rateForDick3) {
		RandomUtils.rateForDick3 = rateForDick3;
	}

	public static double getRateForDick4() {
		return rateForDick4;
	}

	public static void setRateForDick4(double rateForDick4) {
		RandomUtils.rateForDick4 = rateForDick4;
	}

	public static double getRateForDick5() {
		return rateForDick5;
	}

	public static void setRateForDick5(double rateForDick5) {
		RandomUtils.rateForDick5 = rateForDick5;
	}

	public static double getRateForDick6() {
		return rateForDick6;
	}

	public static void setRateForDick6(double rateForDick6) {
		RandomUtils.rateForDick6 = rateForDick6;
	}

	/**
	 * 按概率返回随机获得的点数
	 * 
	 * @return int
	 *
	 */
	public static int dickRandom() {
		double randomNumber;
		randomNumber = Math.random();
		if (randomNumber >= 0 && randomNumber <= rateForDick1) {
			return 1;
		} else if (randomNumber >= rateForDick1 && randomNumber <= rateForDick1 + rateForDick2) {
			return 2;
		} else if (randomNumber >= rateForDick1 + rateForDick2 && randomNumber <= rateForDick1 + rateForDick2 + rateForDick3) {
			return 3;
		} else if (randomNumber >= rateForDick1 + rateForDick2 + rateForDick3
				&& randomNumber <= rateForDick1 + rateForDick2 + rateForDick3 + rateForDick4) {
			return 4;
		} else if (randomNumber >= rateForDick1 + rateForDick2 + rateForDick3 + rateForDick4
				&& randomNumber <= rateForDick1 + rateForDick2 + rateForDick3 + rateForDick4 + rateForDick5) {
			return 5;
		} else if (randomNumber >= rateForDick1 + rateForDick2 + rateForDick3 + rateForDick4 + rateForDick5
				&& randomNumber <= rateForDick1 + rateForDick2 + rateForDick3 + rateForDick4 + rateForDick5 + rateForDick6) {
			return 6;
		}
		return 0;
	}

	/**
	 * 
	 * @Description:返回范围内的随机数
	 * @param min
	 * @param max
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月8日 上午10:57:10
	 */
	public static Integer getRandomNumberByRange(int min, int max) {
		Random rand = new Random();
		try {
			return rand.nextInt((max - min) + 1) + min;
		} catch (Exception e) {
			logger.error("返回范围内的随机数失败 min={}, max={}", min, max, e);
		}
		return 0;
	}

	/**
	 * 
	 * @Description:拆分成指定数量的整数（存在重复结果）
	 * @param totalAmount
	 * @param num
	 * @author: wangyanji
	 * @time:2016年1月6日 下午5:33:30
	 */
	public static List<Integer> calcByQuotaTotalAmountAndSplitNum(int totalAmount, int splitNum, int minSplit) {
		// 需要生成 num-1个随机数
		int randomNum = splitNum - 1;
		int unitNum = minSplit * 2;
		int randomAmount = totalAmount - unitNum;
		Set<Integer> randomContainer = Sets.newTreeSet();
		Random rand = new Random();
		while (randomContainer.size() < randomNum) {
			randomContainer.add(rand.nextInt(randomAmount) + unitNum);
		}
		// 整合结果
		List<Integer> resultList = Lists.newArrayListWithCapacity(splitNum);
		int index = 0;
		for (int i : randomContainer) {
			if (resultList.isEmpty()) {
				// 生成第一个，直接取第一个就可以
				resultList.add(i);
			} else {
				// 当前随机数-上一个随机数
				resultList.add(i - index);
			}
			index = i;
		}
		// 生成最后一个需要最大值-随机最后一个数
		resultList.add(totalAmount - index);
		return resultList;
	}

	/**
	 * 
	 * @Description:根据剩余总额，和剩余次数生成一个随机数
	 * @param amount
	 * @param num
	 * @param min
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月29日 下午5:56:55
	 */
	public static BigDecimal getRandomInList(BigDecimal amount, int num, int min) {
		int max = amount.divide(new BigDecimal(num), 1, BigDecimal.ROUND_DOWN).intValue() * 2;
		if (max < 1)
			return new BigDecimal(min);
		int thisAmount = getRandomNumberByRange(min, max);
		return new BigDecimal(thisAmount);
	}
	
	//统计出现概率的计数变量
    //评估函数: 计算运行不同的次数 , 每种结果的出现概率
	// 3种选择结果
	public static int count_1 = 1;
	public static int count_2 = 2;
	public static int count_3 = 3;
    
	public static List getRendomNumber(int count,List<Integer> probability) {
    	Random r = new Random();
    	int random = 0;
        int num;
        List tempList=Lists.newArrayList();
        for(int i=0; i<count; i++) {
            num = r.nextInt(100) + 1;    //让随机数在1~100间产生随机数
            if(num <= probability.get(0)) {        //40%
                count_1++;
                random=1;
            } else if(num <= probability.get(1)) { //40%
                count_2++;
                random=2;
            } else if(num <= probability.get(2)) { //20%
                count_3++;
                random=3;
            }
            tempList.add(random);
        }
        //计数器清零
         count_1 = count_2 = count_3 = 0;
         return tempList;
    }
	public static void main(String[] args) {
		//System.out.println(getRendomNumber(2));
	}
//	public static void main(String[] args) {
//		int number = 1;
//		int min = 1;
//		while (number <= 1000) {
//			BigDecimal amount = new BigDecimal(80).subtract(new BigDecimal(min));
//			int num = 8;
//			int totalAmount = 0;
//			for (int i = 0; i < num; i++) {
//				BigDecimal unitAmount = null;
//				if (i < num - 1) {
//					unitAmount = getRandomInList(amount, num - i, min);
//					amount = amount.subtract(unitAmount);
//				} else {
//					unitAmount = amount.add(new BigDecimal(min));
//				}
//				totalAmount += unitAmount.intValue();
//				System.out.println("第" + (i + 1) + "次的红包金额" + "=" + unitAmount.toString() + "...总额=" + totalAmount);
//			}
//			System.out.println("总计第" + number + "次总额=" + totalAmount);
//			number++;
//		}
//	}
}

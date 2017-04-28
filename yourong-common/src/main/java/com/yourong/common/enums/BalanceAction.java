package com.yourong.common.enums;

//余额，可用余额，计算
	public enum BalanceAction {
		/****  余额减少，可用余额不变**/
		balance_subtract(1),
		/**** 余额减少，可用余额也减少**/
		balance_subtract_Available_subtract(2),
		/**直接修改*/
		balance_Available(3),
		/** 余额 和 可用余额增加*/
		balance_Add_Available_add(4),
		/** 余额不变， 可用余额减少*/
		balance_Available_subtract(5),
		/** 余额不变， 可用余额增加*/
		balance_Available_add(6),
		
		/**冻结库存增加**/
		balance_Good_freeze_stock(7),
		/**冻结库存减少**/
		balance_Good_unfreeze_stock(8),
		/**总库存减少，冻结库存减少**/
		balance_Good_total_subtract(9);
		
		private int action;

		private BalanceAction(int action) {
			this.action = action;
		}
		public int getAction() {
			return action;
		}
	}
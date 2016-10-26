import java.util.*;

public class Apriori {

	public static void main (String args[]) {

		// Get input
		Scanner terminal = new Scanner(System.in);
		System.out.print("Number of transactions: ");
		int numberOfTransactions = Integer.parseInt(terminal.nextLine());
		System.out.println("Enter transactions separated by new line and items separated by spaces:");

		ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();

		ArrayList<ArrayList<String>> prevItemSetsWithMinSupportCount = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < numberOfTransactions; i++) {
			ArrayList<String> transaction = new ArrayList<String>();
			String str = terminal.nextLine();
			String arr[] = str.split(" ");
			for (int j = 0; j < arr.length; j++) transaction.add(arr[j]);
			transactions.add(transaction);
		}

		System.out.print("Minumum support count: ");
		int minSupportCount = Integer.parseInt(terminal.nextLine());

		// Get all items
		ArrayList<String> items = getUniqueItems(transactions);

		int x = 0; // x is the number of elements in the item-ets to consider
		while (true) {

			// Consider one more item than the last iteration
			x++;

			// List of support count of each itemset
			ArrayList<Integer> supportCountList = new ArrayList<Integer>();

			// Get permuted itemsets with items. There will be x elements in each itemset.
			ArrayList<ArrayList<String>> itemSets = getItemSets(items, x);

			// Calculate each itemset's support count
			for (ArrayList<String> itemSet : itemSets) {

				int count = 0;
				for (ArrayList<String> transaction : transactions) {
					if (existsInTransaction(itemSet, transaction)) count++;
				}
				supportCountList.add(count);
			}

			// Out of all the itemsets, get the itemsets with
			// support count greater than or equal to minSupportCount
			ArrayList<ArrayList<String>> itemSetsWithMinSupportCount = getItemSetsWithMinSupportCount(itemSets, supportCountList, minSupportCount);

			// No itemSetsWithMinSupportCount exist
			if (itemSetsWithMinSupportCount.size() == 0) {
				System.out.print("The itemset(s) that are the most frequent itemset(s): ");
				System.out.println(prevItemSetsWithMinSupportCount);
				break;
			}

			// IMPROVING APRIORI USING TRANSACTION REDUCTION
			/*
				Will remove transactions that do not contain any frequent itemsets.
			*/

			for (int i = 0; i < transactions.size(); i++) {
				ArrayList<String> transaction = transactions.get(i);
				boolean contains = false;
				for (ArrayList<String> itemSet : itemSetsWithMinSupportCount) {
					if (transactionContainsItemset(transaction, itemSet)) {
						contains = true;
						break;
					}
				}

				if (!contains) {
					transactions.remove(transaction);
					i--;
				}
			}

			// END OF IMPROVEMENT

			items = getUniqueItems(itemSetsWithMinSupportCount);

			prevItemSetsWithMinSupportCount = itemSetsWithMinSupportCount;
		}

	}

	// Returns the list of unqiue items from a list of transactions
	private static ArrayList<String> getUniqueItems (ArrayList<ArrayList<String>> data) {
		ArrayList<String> toReturn = new ArrayList<String>();

		for (ArrayList<String> transaction : data) {
			for (String item : transaction) {
				if (!toReturn.contains(item)) toReturn.add(item);
			}
		}

		Collections.sort(toReturn);
		return toReturn;
	}

	// Returns a list of itemsets, where each itemset has x number of items
	private static ArrayList<ArrayList<String>> getItemSets (ArrayList<String> items, int number) {
		if (number == 1) {

			// Return ArrayList of (ArrayList with one item)
			ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();
			for (String item : items) {
				ArrayList<String> aList = new ArrayList<String>();
				aList.add(item);
				toReturn.add(aList);
			}
			return toReturn;

		} else {

			int size = items.size();

			ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();

			for (int i = 0; i < size; i++) {

				// Copy items to _items
				ArrayList<String> _items = new ArrayList<String>();
				for (String item : items) {
					_items.add(item);
				}

				// Get item at i-th position
				String thisItem = items.get(i);

				// Remove items upto i, inclusive
				for (int j = 0; j <= i; j++) {
					_items.remove(0);
				}

				// Get permutations of the remaining items
				ArrayList<ArrayList<String>> permutationsBelow = getItemSets(_items, number - 1);

				// Add thisItem to each permutation and add the permutation to toReturn
				for (ArrayList<String> aList : permutationsBelow) {
					aList.add(thisItem);
					Collections.sort(aList);
					toReturn.add(aList);
				}

			}

			return toReturn;

		}
	}

	// Check if all items exist in a transaction
	private static boolean existsInTransaction (ArrayList<String> items, ArrayList<String> transaction) {
		for (String item : items) {
			if (!transaction.contains(item)) return false;
		}
		return true;
	}

	// Returns itemsets with support count greater than or equal to minimum support count
	private static ArrayList<ArrayList<String>> getItemSetsWithMinSupportCount (
		ArrayList<ArrayList<String>> itemSets, ArrayList<Integer> count, int minSupportCount) {

		ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < count.size(); i++) {
			int c = count.get(i);
			if (c >= minSupportCount) {
				toReturn.add(itemSets.get(i));
			}
		}

		return toReturn;
	}

	// Returns whether or not the transaction contains all items in the itemset, i.e. whether or not the itemset is a subset of the transaction
	private static boolean transactionContainsItemset (ArrayList<String> transaction, ArrayList<String> itemSet) {

		// More items in the itemset than in the transaction
		if (transaction.size() < itemSet.size()) return false;

		boolean toReturn = true;

		for (String item : itemSet) {
			if (!transaction.contains(item)) {
				toReturn = false;
				break;
			}
		}

		return toReturn;
	}

}
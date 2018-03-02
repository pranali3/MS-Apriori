# Created by team: Pratik Anil Kshirsagar (pkshir2@uic.edu) and Pranali Loke(ploke2@uic.edu)

import os
import itertools

# Initialise the required data structures and literals.

support_difference_constraint = 0.0
Minimum_Support = dict()
temp = dict()  # to retrieve file input and store it into appropriate data structures.
MIS_Sorted_Values = dict()  # to store sorted MIS values.
Item_List = list()
Item_Count = dict()

Transaction_List = list()
Transaction_Count = 0

List_Of_Items = list()
Frequent_List = list([] for z1 in range(25))  # List of lists to store frequent itemsets.
Candidate_List = list([] for z2 in range(25))  # List of lists to store candidate itemsets.

Candidate_Collection = dict()
Cannot_List = list()
Must_Have = list()
Tail_Count = {}

current_directory = os.path.dirname(__file__)  # directory of current file
print(current_directory)

# All functions according to MSApriori

def main():
        Read_Data()
        Initial_Pass()
        Fk_equals_to_1()
        Fk_greater_than_1()
        Cannot_Be_Together()
        if Must_Have != []:  # Must_Have is not empty
            Must_Have_Constraint()
        Output_To_File()

# Reading data

def Read_Data():
        # To read the MIS values and support difference constraint (SDC) from parameter-file.txt
        global Minimum_Support, Transaction_Count, Item_List, Item_Count, support_difference_constraint, Transaction_List, Cannot_List, Must_Have

        rel_path_params = "parameters/para1-2.txt"
        abs_filepath_params = os.path.join(current_directory, rel_path_params)
        paramFileHandle = open(abs_filepath_params)



        for element in paramFileHandle:

            if element.find('cannot_be_together') != -1:

                Cannot_List = element.replace(' ', '').replace('cannot_be_together:', '').replace('{', '[').replace('}', ']').replace("'",'a').rstrip().split('a')
                Cannot_List = Cannot_List[0].replace('],[',' ').replace('[','').replace(']','').split()
                for j in range(len(Cannot_List)):
                    Cannot_List[j] = list(map(int, Cannot_List[j].split(',')))

            if element.find('must-have') != -1:
                Must_Have = element.replace('must-have:', '').replace(' ','').rstrip().split('or')
                Must_Have = list(map(int, Must_Have))

            if element.find('MIS') != -1:
                temp = element.replace(' ','').replace('MIS','').replace('(', '').replace(')', '').rstrip().split('=')
                Minimum_Support[int(temp[0])] = float(temp[1])

            if element.find('SDC') != -1:
                support_difference_constraint = float(element.replace(' ','').rstrip().split('=')[1])

        items = sorted(Minimum_Support, key = Minimum_Support.__getitem__)

        for item in items:
            Item_List.append(int(item))
            Item_Count[int(item)] = 0

        # To read the Transactions from input-data.txt
        rel_path_data = "data/data4.txt"
        abs_filepath_data = os.path.join(current_directory, rel_path_data)
        inputFileHandle = open(abs_filepath_data)

        for element in inputFileHandle:
            Transaction_List.append(list())
            transaction = element.replace(' ', '').replace('{', '').replace('}', '').replace('<', '').replace('>', '').split(',')
            for t in transaction:
                Transaction_List[len(Transaction_List)-1].append(int(t))
                if Item_Count.get(int(t)) is not None:
                    Item_Count[int(t)] = Item_Count.get(int(t)) + 1

        Transaction_Count = len(Transaction_List)

# Making the initial pass

def Initial_Pass():
    global List_Of_Items
    for element in range(len(Item_List)):
        if element == 0:
            List_Of_Items.append(Item_List[0])
        else:
            if(Item_Count.get(Item_List[element]) / Transaction_Count) >= Minimum_Support.get(Item_List[0]):
                List_Of_Items.append(Item_List[element])

        for element in range(len(List_Of_Items)):
            MIS_Sorted_Values[List_Of_Items[element]] = element


#Generating candidates for level 2

def level2CandidateGeneration():
    global Candidate_List, List_Of_Items
    for l in range(0, len(List_Of_Items)):
        if (Item_Count[List_Of_Items[l]] / Transaction_Count) >= Minimum_Support[List_Of_Items[l]]:
            for h in range(l + 1, len(List_Of_Items)):
                if(Item_Count[List_Of_Items[h]] / Transaction_Count) >= Minimum_Support[List_Of_Items[l]] and abs((Item_Count[List_Of_Items[h]] / Transaction_Count) - (Item_Count[List_Of_Items[l]] / Transaction_Count)) <= support_difference_constraint:
                    Candidate_List[2].append(list())
                    Candidate_List[2][len(Candidate_List[2])-1].append(List_Of_Items[l])
                    Candidate_List[2][len(Candidate_List[2])-1].append(List_Of_Items[h])
    Candidate_List[2].sort(key=lambda row: row[1])

# Finding subsets

def findSubsets(S, s):
        return list(set(itertools.combinations(S, s)))


# Candidate generation for k > 2

def MSCandidateGeneration(n):
        s = n - 1
        k = 0
        for element in range(0, len(Frequent_List[s])):
            for j in range(0, len(Frequent_List[s])):
                while(k < s-1) and (Frequent_List[s][element][k] == Frequent_List[s][j][k]):
                    k = k + 1

                if k == s - 1:
                    if(MIS_Sorted_Values[Frequent_List[s][element][k]] < MIS_Sorted_Values[Frequent_List[s][j][k]]) and (abs((Item_Count[Frequent_List[s][element][k]] / Transaction_Count) - (Item_Count[Frequent_List[s][j][k]] / Transaction_Count)) <= support_difference_constraint):
                        Candidate_List[s + 1].append(list(Frequent_List[s][element]))
                        Candidate_List[s + 1][len(Candidate_List[s + 1])-1].append(Frequent_List[s][j][k])
                        subset = findSubsets(Candidate_List[s + 1][len(Candidate_List[s + 1])-1], s)
                        for sub in range(0, len(subset)):
                            if not Candidate_List[s + 1]:
                                if (bool(Candidate_List[s + 1][len(Candidate_List[s + 1])-1][0]) in subset[sub]) or (Minimum_Support[Candidate_List[s + 1][len(Candidate_List[s + 1])-1][1]] == Minimum_Support[Candidate_List[s + 1][len(Candidate_List[s + 1])-1][0]]):
                                    if bool(subset[sub]) not in Frequent_List[s]:
                                        Candidate_List[s + 1][len(Candidate_List[s + 1])-1].remove()

                k = 0

# Adding 1-itemsets to the Frequent_List

def Fk_equals_to_1():
        global Frequent_List
        if not Frequent_List[1]:
            for element in range(len(List_Of_Items)):
                if(Item_Count.get(List_Of_Items[element])/Transaction_Count) >= Minimum_Support.get(List_Of_Items[element]):
                    Frequent_List[1].append([List_Of_Items[element]])

# Adding k>1 Itemsets to the Frequent_List

def Fk_greater_than_1():
    global Frequent_List, Candidate_Collection, Tail_Count
    k = 2
    while True:
        if not Frequent_List[k - 1]:
            break

        if k == 2:
            level2CandidateGeneration()
        else:
            MSCandidateGeneration(k)

        for t in Transaction_List:
            for c in Candidate_List[k]:
                if set(c).issubset(set(t)):
                    if Candidate_Collection.get(tuple(c)) is None:
                        Candidate_Collection[tuple(c)] = 1
                    else:
                        Candidate_Collection[tuple(c)] = Candidate_Collection.get(tuple(c)) + 1

                if set(c[1:]).issubset(set(t)):
                    if Tail_Count.get(tuple(c)) is None:
                        Tail_Count[tuple(c)] = 1
                    else:
                        Tail_Count[tuple(c)] = Tail_Count.get(tuple(c)) + 1

        for c in Candidate_List[k]:
            if Candidate_Collection.get(tuple(c)) is not None:
                if Candidate_Collection.get(tuple(c)) / Transaction_Count >= Minimum_Support[c[0]]:
                    Frequent_List[k].append(c[:])
        k = k + 1


# Checking the cannot-be-together constraint

def Cannot_Be_Together():
        global Cannot_List, Frequent_List
        for fListIndex in range(len(Frequent_List)):
            listIndex = len(Frequent_List[fListIndex])-1
            while listIndex > -1:
                subset = Frequent_List[fListIndex][listIndex]
                for element in range(len(Cannot_List)):
                    if set(Cannot_List[element]) <= set(subset):
                        Frequent_List[fListIndex].pop(Frequent_List[fListIndex].index(subset))
                        break
                listIndex = listIndex - 1

# Checking the must-have constraints.

def Must_Have_Constraint():
        global Must_Have, Frequent_List
        for fListIndex in range(len(Frequent_List)):
            listIndex = len(Frequent_List[fListIndex])-1
            while listIndex > -1:
                subset = Frequent_List[fListIndex][listIndex]
                if set(Must_Have).isdisjoint(subset):
                    Frequent_List[fListIndex].pop(Frequent_List[fListIndex].index(subset))
                listIndex = listIndex - 1

# Writing output to file.

def Output_To_File():
        global Frequent_List, Tail_Count, Item_Count, Candidate_Collection
        Frequent_List = [x for x in Frequent_List if x != []]

        rel_path_results = "output.txt"
        abs_filepath_results = os.path.join(current_directory, rel_path_results)
        outputFileHandle = open(abs_filepath_results, "w")

        count = 0
        print("Frequent 1-itemsets")
        outputFileHandle.write("\nFrequent 1-itemsets\n")
        if Frequent_List is not None:
            for t in Frequent_List[0]:
                count = count + 1
                print('\t' + str(Item_Count[t[0]]) + ' : { ' + str(t[0]) + ' }')
                outputFileHandle.write('\t' + str(Item_Count[t[0]]) + ' : { ' + str(t[0]) + ' }\n')
        print("\tTotal number of frequent 1-itemsets = " + str(count))
        outputFileHandle.write("\tTotal number of frequent 1-itemsets = " + str(count) + "\n")


        for element in range(1,len(Frequent_List)):
            print('\n\nFrequent',str(element+1) + '-itemsets')
            outputFileHandle.write('\n\nFrequent' + str(element+1) + '-itemsets\n')
            count = 0
            for t in Frequent_List[element]:
                Trans_Count = 0
                for j in range(len(Transaction_List)):
                    if set(Transaction_List[j]) >= set(t):
                        Trans_Count = Trans_Count + 1
                count = count + 1
                print('\t',Trans_Count, ' : {',str(t).replace("[", "").replace("]", ""),'}')
                outputFileHandle.write('\t' + str(Trans_Count) + ' : {' + str(t).replace("[", "").replace("]", "") + '}\n')
                print('Tailcount =', Tail_Count[tuple(t)])
                outputFileHandle.write('Tailcount = ' + str(Tail_Count[tuple(t)]) + "\n")
            print('\n\tTotal number of frequent', str(element+1) + '-itemsets = ' ,count)
            outputFileHandle.write('\n\tTotal number of frequent ' + str(element+1) + '-itemsets = '  + str(count) + "\n")


if __name__ == "__main__":
    main()


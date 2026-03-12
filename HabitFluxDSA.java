import java.util.*;

/**
 * ================================================================
 *  HABITFLUX - Interactive DSA Implementation in Java
 *  Smart Routine Monitor | Input-driven, mirrors the website flow
 * ================================================================
 * CO1: Algorithmic efficiency - Big-O, Omega, Theta analysis.
 *      Linear search, binary search, bubble sort, selection sort,
 *      insertion sort, merge sort, quick sort.
 *
 * CO2: ADTs using singly, doubly, and circular linked lists.
 *      Insert, delete, search, traverse, reverse, cycle detection.
 *
 * CO3: Stack (undo), queue (scheduling), circular queue, deque,
 *      max-heap (most missed task), priority queue (task priority).
 *
 * CO4: Hash table with chaining (user auth), open addressing,
 *      Java HashMap to group routines by date. Java Collections.
 *
 * CO5: Drift analysis, productivity scoring, undo system,
 *      notification alerts — practical linear DS applications.
 *
 * CO6: Full end-to-end HABITFLUX simulation combining all DS.
 * ================================================================
 */
public class HabitFluxDSA {

    static Scanner sc = new Scanner(System.in);

    // ================================================================
    // MODELS
    // ================================================================

    // Routine - mirrors a task row in the HABITFLUX dashboard table
    static class Routine {
        int     id, plannedStart, plannedEnd, actualStart, actualEnd;
        String  task, date, priority;
        boolean logged;

        Routine(int id, String task, String date,
                int plannedStart, int plannedEnd, String priority) {
            this.id = id; this.task = task; this.date = date;
            this.plannedStart = plannedStart; this.plannedEnd = plannedEnd;
            this.priority = priority;
            this.actualStart = -1; this.actualEnd = -1; this.logged = false;
        }

        int plannedDuration() { return plannedEnd - plannedStart; }
        int actualDuration()  { return logged ? actualEnd - actualStart : 0; }
        int drift()           { return logged ? actualDuration() - plannedDuration() : 0; }
    }

    // User - mirrors a registered account (index.html signup form)
    static class User {
        String name, email, password;
        User(String n, String e, String p) { name=n; email=e; password=p; }
    }

    // ================================================================
    // CO1: Classical searching and sorting algorithms.
    //      Used to sort the routines table and search by name/ID.
    // ================================================================

    // CO1: Linear Search - O(n) | unsorted list lookup
    static Routine linearSearch(Routine[] arr, String name) {
        for (Routine r : arr)
            if (r.task.equalsIgnoreCase(name)) return r;
        return null;
    }

    // CO1: Binary Search - O(log n) | requires sorted array
    static Routine binarySearch(Routine[] arr, int id) {
        int lo=0, hi=arr.length-1;
        while (lo<=hi) {
            int mid=(lo+hi)/2;
            if      (arr[mid].id==id) return arr[mid];
            else if (arr[mid].id<id)  lo=mid+1;
            else                      hi=mid-1;
        }
        return null;
    }

    // CO1: Bubble Sort - O(n^2) | sort by drift
    static void bubbleSort(Routine[] arr) {
        int n=arr.length;
        for (int i=0;i<n-1;i++)
            for (int j=0;j<n-i-1;j++)
                if (arr[j].drift()>arr[j+1].drift()) {
                    Routine t=arr[j]; arr[j]=arr[j+1]; arr[j+1]=t;
                }
    }

    // CO1: Selection Sort - O(n^2) | sort by planned start time
    static void selectionSort(Routine[] arr) {
        int n=arr.length;
        for (int i=0;i<n-1;i++) {
            int min=i;
            for (int j=i+1;j<n;j++)
                if (arr[j].plannedStart<arr[min].plannedStart) min=j;
            Routine t=arr[i]; arr[i]=arr[min]; arr[min]=t;
        }
    }

    // CO1: Insertion Sort - O(n^2) worst, O(n) best | sort by start time
    static void insertionSort(Routine[] arr) {
        for (int i=1;i<arr.length;i++) {
            Routine key=arr[i]; int j=i-1;
            while (j>=0 && arr[j].plannedStart>key.plannedStart) {
                arr[j+1]=arr[j]; j--;
            }
            arr[j+1]=key;
        }
    }

    // CO1: Merge Sort - O(n log n) | stable, for history page
    static void mergeSort(Routine[] arr, int l, int r) {
        if (l<r) {
            int m=(l+r)/2;
            mergeSort(arr,l,m); mergeSort(arr,m+1,r);
            merge(arr,l,m,r);
        }
    }
    static void merge(Routine[] arr, int l, int m, int r) {
        Routine[] L=Arrays.copyOfRange(arr,l,m+1);
        Routine[] R=Arrays.copyOfRange(arr,m+1,r+1);
        int i=0,j=0,k=l;
        while (i<L.length && j<R.length)
            arr[k++]=(L[i].plannedStart<=R[j].plannedStart)?L[i++]:R[j++];
        while (i<L.length) arr[k++]=L[i++];
        while (j<R.length) arr[k++]=R[j++];
    }

    // CO1: Quick Sort - O(n log n) avg | sort by drift
    static void quickSort(Routine[] arr, int lo, int hi) {
        if (lo<hi) {
            int pi=partition(arr,lo,hi);
            quickSort(arr,lo,pi-1); quickSort(arr,pi+1,hi);
        }
    }
    static int partition(Routine[] arr, int lo, int hi) {
        int pivot=arr[hi].drift(), i=lo-1;
        for (int j=lo;j<hi;j++)
            if (arr[j].drift()<=pivot) {
                i++; Routine t=arr[i]; arr[i]=arr[j]; arr[j]=t;
            }
        Routine t=arr[i+1]; arr[i+1]=arr[hi]; arr[hi]=t;
        return i+1;
    }

    // ================================================================
    // CO2: Linked List node (shared by all three list types)
    // ================================================================
    static class Node {
        Routine data; Node next, prev;
        Node(Routine d) { data=d; }
    }

    // CO2: Singly Linked List - stores routines for a day
    // insertBack O(n) | delete/search O(n) | reverse O(n)
    // Floyd's cycle detection O(n)
    static class SinglyLinkedList {
        Node head;
        void insertBack(Routine r) {
            Node n=new Node(r);
            if (head==null){head=n;return;}
            Node c=head; while(c.next!=null)c=c.next; c.next=n;
        }
        void delete(int id) {
            if (head==null)return;
            if (head.data.id==id){head=head.next;return;}
            Node c=head;
            while(c.next!=null&&c.next.data.id!=id)c=c.next;
            if(c.next!=null)c.next=c.next.next;
        }
        Node search(String name) {
            Node c=head;
            while(c!=null){if(c.data.task.equalsIgnoreCase(name))return c;c=c.next;}
            return null;
        }
        void reverse() {
            Node prev=null,c=head;
            while(c!=null){Node nx=c.next;c.next=prev;prev=c;c=nx;}
            head=prev;
        }
        boolean hasCycle() {
            Node s=head,f=head;
            while(f!=null&&f.next!=null){s=s.next;f=f.next.next;if(s==f)return true;}
            return false;
        }
        void print() {
            Node c=head;
            while(c!=null){
                System.out.printf("  %-20s  [%s]  %s - %s  Planned: %dm%n",
                    c.data.task, c.data.priority,
                    minsToTime(c.data.plannedStart), minsToTime(c.data.plannedEnd),
                    c.data.plannedDuration());
                c=c.next;
            }
        }
    }

    // CO2: Doubly Linked List - history page navigation
    // insertBack O(1) | delete O(n) | forward/backward traversal
    static class DoublyLinkedList {
        Node head, tail;
        void insertBack(Routine r) {
            Node n=new Node(r);
            if(tail==null){head=tail=n;return;}
            n.prev=tail; tail.next=n; tail=n;
        }
        void traverseForward() {
            Node c=head;
            while(c!=null){
                System.out.printf("  %-20s  Drift: %s%n",
                    c.data.task, c.data.logged ? String.format("%+dm",c.data.drift()) : "--");
                c=c.next;
            }
        }
        void traverseBackward() {
            Node c=tail;
            System.out.print("  Reverse: ");
            while(c!=null){System.out.print(c.data.task+(c.prev!=null?" -> ":""));c=c.prev;}
            System.out.println();
        }
    }

    // CO2: Circular Linked List - weekly day cycling
    static class CircularLinkedList {
        Node head;
        void insert(Routine r) {
            Node n=new Node(r);
            if(head==null){head=n;n.next=head;return;}
            Node c=head; while(c.next!=head)c=c.next;
            c.next=n; n.next=head;
        }
        int size() {
            if(head==null)return 0;
            int c=1; Node cur=head.next;
            while(cur!=head){c++;cur=cur.next;} return c;
        }
        void traverse(int steps) {
            if(head==null)return;
            Node c=head; int total=size();
            for(int i=0;i<steps;i++){
                System.out.printf("  Day %-2d  ->  %s%n",(i%total)+1,c.data.task);
                c=c.next;
            }
        }
    }

    // ================================================================
    // CO3: Stack, Queue, Circular Queue, Deque, Max-Heap, Priority Queue
    // ================================================================

    // CO3: Stack - LIFO | undo last added routine
    static class RoutineStack {
        Routine[] s; int top=-1;
        RoutineStack(int cap){s=new Routine[cap];}
        void    push(Routine r){if(top<s.length-1)s[++top]=r;}
        Routine pop()          {return top==-1?null:s[top--];}
        Routine peek()         {return top==-1?null:s[top];}
        boolean isEmpty()      {return top==-1;}
    }

    // CO3: Queue - FIFO | task scheduling in planned order
    static class RoutineQueue {
        static class QN{Routine d;QN n;QN(Routine r){d=r;}}
        QN front,rear;
        void enqueue(Routine r){
            QN n=new QN(r);
            if(rear==null){front=rear=n;return;}
            rear.n=n;rear=n;
        }
        Routine dequeue(){
            if(front==null)return null;
            Routine r=front.d;front=front.n;
            if(front==null)rear=null;return r;
        }
        boolean isEmpty(){return front==null;}
    }

    // CO3: Circular Queue - fixed-size rolling reminder window
    static class CircularQueue {
        Routine[] q; int front,rear,size,cap;
        CircularQueue(int cap){q=new Routine[cap];this.cap=cap;}
        void enqueue(Routine r){if(size==cap)return;q[rear]=r;rear=(rear+1)%cap;size++;}
        Routine dequeue(){if(size==0)return null;Routine r=q[front];front=(front+1)%cap;size--;return r;}
        boolean isEmpty(){return size==0;}
    }

    // CO3: Deque - O(1) both ends | 7-day drift sliding window
    static class RoutineDeque {
        Deque<Routine> dq=new ArrayDeque<>();
        void    addFront(Routine r){dq.addFirst(r);}
        void    addRear(Routine r) {dq.addLast(r);}
        Routine removeFront()      {return dq.pollFirst();}
        Routine removeRear()       {return dq.pollLast();}
        int     size()             {return dq.size();}
    }

    // CO3: Max-Heap - O(log n) | surfaces highest-drift task
    static class MaxDriftHeap {
        Routine[] h; int size=0;
        MaxDriftHeap(int cap){h=new Routine[cap];}
        void insert(Routine r){
            h[size++]=r; int i=size-1;
            while(i>0&&h[(i-1)/2].drift()<h[i].drift()){
                Routine t=h[(i-1)/2];h[(i-1)/2]=h[i];h[i]=t;i=(i-1)/2;
            }
        }
        Routine extractMax(){
            if(size==0)return null;
            Routine max=h[0];h[0]=h[--size];heapify(0);return max;
        }
        void heapify(int i){
            int lg=i,l=2*i+1,r=2*i+2;
            if(l<size&&h[l].drift()>h[lg].drift())lg=l;
            if(r<size&&h[r].drift()>h[lg].drift())lg=r;
            if(lg!=i){Routine t=h[i];h[i]=h[lg];h[lg]=t;heapify(lg);}
        }
    }

    // CO3: Priority Queue - poll O(log n) | High before Medium/Low
    static PriorityQueue<Routine> buildPQ(List<Routine> list) {
        Map<String,Integer> rank=Map.of("High",3,"Medium",2,"Low",1);
        PriorityQueue<Routine> pq=new PriorityQueue<>(
            (a,b)->rank.get(b.priority)-rank.get(a.priority));
        pq.addAll(list); return pq;
    }

    // ================================================================
    // CO4: Hash Tables + Java Collections
    // ================================================================

    // CO4: Hash Table - Separate Chaining | O(1) avg put/get
    // Mirrors hf_users in localStorage (index.html)
    static class UserHashTable {
        static final int SIZE=16;
        LinkedList<User>[] table;
        @SuppressWarnings("unchecked")
        UserHashTable(){
            table=new LinkedList[SIZE];
            for(int i=0;i<SIZE;i++)table[i]=new LinkedList<>();
        }
        int hash(String k){return Math.abs(k.hashCode())%SIZE;}
        void put(User u){int i=hash(u.email);table[i].removeIf(x->x.email.equals(u.email));table[i].add(u);}
        User get(String e){for(User u:table[hash(e)])if(u.email.equals(e))return u;return null;}
        boolean auth(String e,String p){User u=get(e);return u!=null&&u.password.equals(p);}
    }

    // CO4: Hash Table - Open Addressing Linear Probing | O(1) avg
    static class OpenAddressTable {
        User[] table; int cap;
        OpenAddressTable(int cap){table=new User[cap];this.cap=cap;}
        int hash(String k){return Math.abs(k.hashCode())%cap;}
        void put(User u){
            int i=hash(u.email);
            while(table[i]!=null&&!table[i].email.equals(u.email))i=(i+1)%cap;
            table[i]=u;
        }
        User get(String e){
            int i=hash(e);
            while(table[i]!=null){if(table[i].email.equals(e))return table[i];i=(i+1)%cap;}
            return null;
        }
    }

    // CO4: Java HashMap - group routines by date | O(1) avg
    static Map<String,List<Routine>> groupByDate(List<Routine> all){
        Map<String,List<Routine>> map=new HashMap<>();
        for(Routine r:all) map.computeIfAbsent(r.date,k->new ArrayList<>()).add(r);
        return map;
    }

    // ================================================================
    // CO5: Practical applications - drift analysis, undo, alerts
    // ================================================================

    // CO5: Drift & productivity analysis - O(n) array traversal
    static void showDashboard(List<Routine> routines) {
        int totalDrift=0, count=0, totalPlanned=0, totalActual=0;
        Routine worst=null;
        for(Routine r:routines){
            totalPlanned+=r.plannedDuration();
            if(!r.logged)continue;
            totalActual+=r.actualDuration();
            totalDrift+=r.drift(); count++;
            if(worst==null||r.drift()>worst.drift())worst=r;
        }
        int avg  = count>0 ? totalDrift/count : 0;
        int prod = totalPlanned>0
            ? (int)((Math.min(totalActual,totalPlanned)*100.0)/totalPlanned) : 0;
        String level = prod>=80?"Excellent":prod>=50?"Good":"Needs Work";

        System.out.println("\n  ----------------------------------------");
        System.out.printf("  Total Planned : %dm%n",  totalPlanned);
        System.out.printf("  Total Actual  : %dm%n",  totalActual);
        System.out.printf("  Total Drift   : %+dm%n", totalDrift);
        System.out.printf("  Avg Drift     : %+dm%n", avg);
        System.out.printf("  Most Missed   : %s%n",   worst!=null?worst.task:"None");
        System.out.printf("  Productivity  : %d%% - %s%n", prod, level);
        System.out.println("  ----------------------------------------");
    }

    // CO5: Notification queue - FIFO drift alerts
    static void sendAlert(String msg) {
        System.out.println("  [Alert]  " + msg);
    }

    // ================================================================
    // HELPERS
    // ================================================================

    // Convert "HH:MM" string to minutes from midnight
    static int toMins(String t) {
        String[] p=t.split(":");
        return Integer.parseInt(p[0])*60+Integer.parseInt(p[1]);
    }

    // Convert minutes to "HH:MM" string
    static String minsToTime(int m) {
        return String.format("%02d:%02d", m/60, m%60);
    }

    // Print the routines table (mirrors dashboard table in app.html)
    static void printTable(List<Routine> list) {
        if(list.isEmpty()){System.out.println("  No routines found.");return;}
        System.out.printf("  %-4s  %-20s  %-12s  %-12s  %-8s  %-8s  %s%n",
            "ID","Task","Planned","Actual","P.Time","A.Time","Drift");
        System.out.println("  " + "-".repeat(75));
        for(Routine r:list){
            String actual = r.logged
                ? minsToTime(r.actualStart)+"-"+minsToTime(r.actualEnd) : "--";
            String atime  = r.logged ? r.actualDuration()+"m"  : "--";
            String drift  = r.logged ? String.format("%+dm",r.drift()) : "--";
            System.out.printf("  %-4d  %-20s  %-12s  %-12s  %-8s  %-8s  %s%n",
                r.id, r.task,
                minsToTime(r.plannedStart)+"-"+minsToTime(r.plannedEnd),
                actual,
                r.plannedDuration()+"m", atime, drift);
        }
    }

    // ================================================================
    // CO6: Full interactive HABITFLUX application
    //      Combines all DS: auth, linked list, queue, heap, sort, map
    // ================================================================
    public static void main(String[] args) {

        // CO4: Hash table (chaining) stores all registered users
        UserHashTable userStore  = new UserHashTable();
        // CO4: Open addressing table as secondary store
        OpenAddressTable oaStore = new OpenAddressTable(32);

        // CO2: Doubly linked list stores full routine history
        DoublyLinkedList history = new DoublyLinkedList();
        // CO2: Singly linked list stores today's routine list
        SinglyLinkedList todayList = new SinglyLinkedList();
        // CO3: Stack for undo last-added routine
        RoutineStack undoStack = new RoutineStack(50);
        // CO3: Deque as 7-day sliding window
        RoutineDeque weekWindow = new RoutineDeque();
        // CO4: HashMap groups routines by date
        List<Routine> allRoutines = new ArrayList<>();

        int routineIdCounter = 1;
        User currentUser = null;

        System.out.println("================================================================");
        System.out.println("  HABITFLUX  -  Smart Routine Monitor");
        System.out.println("================================================================");

        // ============================================================
        // STEP 1: REGISTER (mirrors index.html signup form)
        // CO4: User stored in hash table using email as key
        // ============================================================
        System.out.println("\n--- Sign Up ---");
        System.out.print("  Full Name  : "); String name  = sc.nextLine().trim();
        System.out.print("  Email      : "); String email = sc.nextLine().trim().toLowerCase();
        System.out.print("  Password   : "); String pass  = sc.nextLine().trim();

        if(userStore.get(email)!=null){
            System.out.println("  Account already exists.");
        } else {
            User newUser = new User(name, email, pass);
            // CO4: Chaining hash table - store user
            userStore.put(newUser);
            // CO4: Open addressing - store same user in parallel
            oaStore.put(newUser);
            System.out.println("  Account created for " + name + ".");
        }

        // ============================================================
        // STEP 2: LOGIN (mirrors index.html login form)
        // CO4: authenticate() does O(1) avg hash lookup
        // ============================================================
        System.out.println("\n--- Login ---");
        System.out.print("  Email    : "); String le = sc.nextLine().trim().toLowerCase();
        System.out.print("  Password : "); String lp = sc.nextLine().trim();

        if(userStore.auth(le, lp)){
            currentUser = userStore.get(le);
            System.out.println("  Welcome, " + currentUser.name + "!");
        } else {
            System.out.println("  Invalid email or password. Exiting.");
            return;
        }

        // ============================================================
        // MAIN APP LOOP (mirrors the app.html dashboard)
        // ============================================================
        boolean running = true;
        String selectedDate = "";

        while(running){
            System.out.println("\n================================================================");
            System.out.println("  Dashboard  |  " + currentUser.name);
            System.out.println("================================================================");
            System.out.println("  1. Select Date");
            System.out.println("  2. Add Routine");
            System.out.println("  3. Log Actual Time");
            System.out.println("  4. View Routines Table");
            System.out.println("  5. View Dashboard Metrics");
            System.out.println("  6. Undo Last Routine");
            System.out.println("  7. Search Routine");
            System.out.println("  8. Sort Routines");
            System.out.println("  9. View History");
            System.out.println("  10. Logout");
            System.out.print("  Choice: ");

            String choice = sc.nextLine().trim();

            switch(choice){

                // ----------------------------------------------------
                // SELECT DATE (mirrors date picker in dashboard header)
                // ----------------------------------------------------
                case "1":
                    System.out.print("  Enter date (YYYY-MM-DD): ");
                    selectedDate = sc.nextLine().trim();
                    System.out.println("  Date set to " + selectedDate);
                    break;

                // ----------------------------------------------------
                // ADD ROUTINE (mirrors Add Routine modal in app.html)
                // CO2: Inserted into singly linked list + doubly list
                // CO3: Pushed onto undo stack | added to deque window
                // CO4: Stored in HashMap grouped by date
                // ----------------------------------------------------
                case "2":
                    if(selectedDate.isEmpty()){
                        System.out.println("  Please select a date first (option 1).");
                        break;
                    }
                    System.out.print("  Task Name  : "); String task  = sc.nextLine().trim();
                    System.out.print("  Start (HH:MM): "); String ps  = sc.nextLine().trim();
                    System.out.print("  End   (HH:MM): "); String pe  = sc.nextLine().trim();
                    System.out.println("  Priority (Low / Medium / High): ");
                    System.out.print("  > "); String prio = sc.nextLine().trim();

                    if(!prio.equals("Low")&&!prio.equals("Medium")&&!prio.equals("High")){
                        System.out.println("  Invalid priority. Defaulting to Medium.");
                        prio="Medium";
                    }
                    if(toMins(pe)<=toMins(ps)){
                        System.out.println("  End time must be after start time.");
                        break;
                    }

                    Routine newR = new Routine(routineIdCounter++, task, selectedDate,
                                               toMins(ps), toMins(pe), prio);
                    // CO2: Add to singly linked list (today's list)
                    todayList.insertBack(newR);
                    // CO2: Add to doubly linked list (history)
                    history.insertBack(newR);
                    // CO3: Push to undo stack
                    undoStack.push(newR);
                    // CO3: Maintain 7-day deque window
                    if(weekWindow.size()==7) weekWindow.removeFront();
                    weekWindow.addRear(newR);
                    // CO4: Add to master list (used by HashMap)
                    allRoutines.add(newR);

                    System.out.println("  \"" + task + "\" added to " + selectedDate + ".");

                    // CO5: Alert if high priority task added
                    if(prio.equals("High"))
                        sendAlert(task + " is a High priority task — don't miss it!");
                    break;

                // ----------------------------------------------------
                // LOG ACTUAL TIME (mirrors Log Activity modal)
                // CO5: Calculates drift and fires alert if > 15 mins
                // ----------------------------------------------------
                case "3":
                    System.out.print("  Task Name to log: "); String logName = sc.nextLine().trim();
                    // CO1: Linear search to find the routine
                    Routine[] arr = allRoutines.toArray(new Routine[0]);
                    Routine target = linearSearch(arr, logName);
                    if(target==null){
                        System.out.println("  Task not found.");
                        break;
                    }
                    System.out.print("  Actual Start (HH:MM): "); String as = sc.nextLine().trim();
                    System.out.print("  Actual End   (HH:MM): "); String ae = sc.nextLine().trim();
                    target.actualStart = toMins(as);
                    target.actualEnd   = toMins(ae);
                    target.logged      = true;

                    int drift = target.drift();
                    System.out.printf("  Logged. Drift: %+dm%n", drift);
                    // CO5: Notification queue alert for major drift
                    if(drift>15)
                        sendAlert("\"" + target.task + "\" ran " + drift + " mins over schedule.");
                    break;

                // ----------------------------------------------------
                // VIEW ROUTINES TABLE (mirrors dashboard table)
                // CO1: Sorted by start time using insertion sort
                // CO2: Reads from singly linked list
                // ----------------------------------------------------
                case "4":
                    if(selectedDate.isEmpty()){System.out.println("  Select a date first.");break;}
                    // CO4: Fetch routines for selected date from HashMap
                    Map<String,List<Routine>> byDate = groupByDate(allRoutines);
                    List<Routine> dayRoutines = byDate.getOrDefault(selectedDate, new ArrayList<>());
                    if(dayRoutines.isEmpty()){System.out.println("  No routines for "+selectedDate);break;}
                    // CO1: Sort by start time before displaying
                    Routine[] dayArr = dayRoutines.toArray(new Routine[0]);
                    insertionSort(dayArr);
                    System.out.println("\n  --- " + selectedDate + " ---");
                    printTable(Arrays.asList(dayArr));
                    break;

                // ----------------------------------------------------
                // DASHBOARD METRICS (mirrors metric cards + AI insight)
                // CO5: Drift analysis, productivity, most-missed task
                // CO3: Max-heap extracts highest-drift task
                // CO3: Priority queue shows high-priority tasks first
                // ----------------------------------------------------
                case "5":
                    if(allRoutines.isEmpty()){System.out.println("  No data yet.");break;}
                    Routine[] all = allRoutines.toArray(new Routine[0]);

                    System.out.println("\n  --- Dashboard Metrics ---");
                    // CO5: Drift analysis
                    showDashboard(allRoutines);

                    // CO3: Max-heap - most missed task
                    MaxDriftHeap heap = new MaxDriftHeap(allRoutines.size()+1);
                    for(Routine r:allRoutines) if(r.logged) heap.insert(r);
                    Routine worst = heap.extractMax();
                    System.out.println("  Most Missed   : " + (worst!=null?worst.task:"None yet"));

                    // CO3: Priority queue - high priority first
                    System.out.println("\n  --- Priority Order ---");
                    PriorityQueue<Routine> pq = buildPQ(allRoutines);
                    while(!pq.isEmpty()){
                        Routine r=pq.poll();
                        System.out.printf("  %-20s  [%s]%n", r.task, r.priority);
                    }

                    // CO3: Circular queue - last 2 reminders
                    System.out.println("\n  --- Latest Reminders ---");
                    CircularQueue cq = new CircularQueue(2);
                    for(Routine r:allRoutines) cq.enqueue(r);
                    while(!cq.isEmpty())
                        System.out.println("  Reminder: " + cq.dequeue().task);
                    break;

                // ----------------------------------------------------
                // UNDO (mirrors delete button on dashboard)
                // CO3: Stack pop removes last added routine
                // ----------------------------------------------------
                case "6":
                    Routine undone = undoStack.pop();
                    if(undone==null){System.out.println("  Nothing to undo.");break;}
                    allRoutines.removeIf(r->r.id==undone.id);
                    System.out.println("  Removed: " + undone.task);
                    System.out.println("  Stack top: " + (undoStack.peek()!=null?undoStack.peek().task:"Empty"));
                    break;

                // ----------------------------------------------------
                // SEARCH (CO1: linear search + binary search by ID)
                // ----------------------------------------------------
                case "7":
                    System.out.println("  1. Search by name  2. Search by ID");
                    System.out.print("  > "); String sOpt = sc.nextLine().trim();
                    Routine[] sArr = allRoutines.toArray(new Routine[0]);

                    if(sOpt.equals("1")){
                        System.out.print("  Task name: "); String sName = sc.nextLine().trim();
                        // CO1: Linear search O(n)
                        Routine res = linearSearch(sArr, sName);
                        if(res!=null)
                            System.out.printf("  Found: %-20s  [%s]  %s%n",
                                res.task, res.priority, res.date);
                        else System.out.println("  Not found.");
                    } else {
                        System.out.print("  Task ID: "); int sid = Integer.parseInt(sc.nextLine().trim());
                        // CO1: Binary search O(log n) - sort by ID first
                        Arrays.sort(sArr, Comparator.comparingInt(r->r.id));
                        Routine res = binarySearch(sArr, sid);
                        if(res!=null)
                            System.out.printf("  Found: %-20s  [%s]  %s%n",
                                res.task, res.priority, res.date);
                        else System.out.println("  Not found.");
                    }
                    break;

                // ----------------------------------------------------
                // SORT (CO1: all 5 sorting algorithms)
                // ----------------------------------------------------
                case "8":
                    if(allRoutines.isEmpty()){System.out.println("  No routines yet.");break;}
                    System.out.println("  1. Insertion Sort (by start time)");
                    System.out.println("  2. Quick Sort     (by drift)");
                    System.out.println("  3. Merge Sort     (by start time)");
                    System.out.println("  4. Bubble Sort    (by drift)");
                    System.out.println("  5. Selection Sort (by start time)");
                    System.out.print("  > "); String srt = sc.nextLine().trim();

                    Routine[] sorted = allRoutines.toArray(new Routine[0]);
                    switch(srt){
                        case "1": insertionSort(sorted); break;
                        case "2": quickSort(sorted,0,sorted.length-1); break;
                        case "3": mergeSort(sorted,0,sorted.length-1); break;
                        case "4": bubbleSort(sorted); break;
                        case "5": selectionSort(sorted); break;
                        default:  System.out.println("  Invalid."); break;
                    }
                    printTable(Arrays.asList(sorted));
                    break;

                // ----------------------------------------------------
                // HISTORY PAGE (CO2: doubly linked list traversal)
                // CO4: HashMap groups all routines by date
                // ----------------------------------------------------
                case "9":
                    System.out.println("\n  --- All History ---");
                    if(allRoutines.isEmpty()){System.out.println("  No history yet.");break;}
                    // CO4: Group by date
                    Map<String,List<Routine>> hist = groupByDate(allRoutines);
                    List<String> dates = new ArrayList<>(hist.keySet());
                    Collections.sort(dates, Collections.reverseOrder());
                    for(String d:dates){
                        System.out.println("\n  " + d);
                        printTable(hist.get(d));
                    }
                    // CO2: Doubly linked list forward + backward
                    System.out.println("\n  --- Doubly Linked List Traversal ---");
                    history.traverseForward();
                    history.traverseBackward();

                    // CO2: Circular list - weekly wrap-around
                    if(!allRoutines.isEmpty()){
                        System.out.println("\n  --- Weekly Cycle (Circular List) ---");
                        CircularLinkedList cll = new CircularLinkedList();
                        for(Routine r:allRoutines) cll.insert(r);
                        cll.traverse(Math.min(allRoutines.size()+2, 10));
                    }
                    break;

                // ----------------------------------------------------
                // LOGOUT
                // ----------------------------------------------------
                case "10":
                    System.out.println("  Goodbye, " + currentUser.name + "!");
                    running=false;
                    break;

                default:
                    System.out.println("  Invalid choice.");
            }
        }

        System.out.println("\n================================================================");
        System.out.println("  HABITFLUX  -  Session Ended");
        System.out.println("================================================================");
        sc.close();
    }
}

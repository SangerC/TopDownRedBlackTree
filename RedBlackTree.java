import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class RedBlackTree<T extends Comparable<? super T>> implements Iterable<RedBlackTree.BinaryNode>{
	private BinaryNode root;
	private int size;
	public int version;
	private int rotationCount;
	private int doubleRotationCount;
	public enum Color {RED, BLACK};
	
	public RedBlackTree(){
		root = null;
		size = 0;
		version = 0;
		rotationCount = 0;
		doubleRotationCount = 0;
	}

	public int size(){
		if(root==null) return 0;
		return size;
	}
	
	public int getRotationCount(){
		return rotationCount;
	}
	
	public int getDoubleRotationCount(){
		return doubleRotationCount;
	}
	
	public boolean insert(T n){
		if(n == null)throw new IllegalArgumentException();
		if(root==null){
			root = new BinaryNode(n);
		}
		else {
			BinaryNode GGP = null;
			BinaryNode GP = null;
			BinaryNode P = null;
			BinaryNode M = root;
			
			while(true){
				testChildrenColors(M,P,GP, GGP);
				int compare = n.compareTo(M.element);
				if(compare==0) {
					root.color=Color.BLACK;
					return false;
				}
				
				if(M.leftChild==null&&M.rightChild==null){
					if(compare<0){
						addToLeft(M,P,GP, n);
						break;
					}
					else if(compare>0){
						addToRight(M,P,GP, n);
						break;
					}
				}
			
				if(M.leftChild==null){
					if(compare<0){
						addToLeft(M,P,GP, n);
						break;
					}
				}
			
				else if(M.rightChild==null){
					if(compare>0){
						addToRight(M,P,GP, n);
						break;
					}
				}
				GGP=GP;
				GP=P;
				P=M;
				if(compare>0){
					M=M.rightChild;
				}
				else{
					M=M.leftChild;
				}
			}
		}
		
		root.color=Color.BLACK;
		size++;
		version++;
		return true;
	}
	
	private void testChildrenColors(BinaryNode M, BinaryNode P, BinaryNode GP, BinaryNode GGP){
		if(M.leftChild==null||M.rightChild==null)return;
		if(M.rightChild.color==Color.RED&&M.leftChild.color==Color.RED){
			M.color=Color.RED;
			M.rightChild.color=Color.BLACK;
			M.leftChild.color=Color.BLACK;
			if(P!=null){
				if(P.color==Color.RED&&GP!=null){
					BinaryNode x;
					if(P.element.compareTo(GP.leftChild.element)==0){
						if(M.element.compareTo(P.leftChild.element)==0) x=GP.singleRightRotation();
						else x=GP.doubleRightRotation();
					}
					else{
						if(M.element.compareTo(P.rightChild.element)==0) x=GP.singleLeftRotation();
						else x=GP.doubleLeftRotation();
					}
					if(GGP==null)root=x;
					else setBySide(GP, GGP, x);
				}
			}
		}
	}
	
	private void addToLeft(BinaryNode M, BinaryNode P, BinaryNode GP, T n){
		M.leftChild = new BinaryNode(n);
		if(M.color==Color.RED&&P!=null){
			BinaryNode x;
			if(P.leftChild==null)x=P.doubleLeftRotation();
			else if(M.element.compareTo(P.leftChild.element)==0)x=P.singleRightRotation();
			else x=P.doubleRightRotation();
			if(GP==null)root=x;
			else setBySide(P, GP, x);
		}
	}
	private void addToRight(BinaryNode M, BinaryNode P, BinaryNode GP, T n){
		M.rightChild = new BinaryNode(n);
		if(M.color==Color.RED&&P!=null){
			BinaryNode x;
			if(P.rightChild==null)x=P.doubleRightRotation();
			else if(M.element.compareTo(P.rightChild.element)==0)x=P.singleLeftRotation();
			else x=P.doubleLeftRotation();
			if(GP==null)root=x;
			else setBySide(P, GP, x);
		}
	}
	private void setBySide(BinaryNode P, BinaryNode GP, BinaryNode x){
		if(GP.rightChild==null)GP.leftChild=x;
		else if(GP.leftChild==null)GP.rightChild=x;
		else{
			if(GP.leftChild.getElement().compareTo(P.getElement())==0)GP.leftChild=x;
			else GP.rightChild=x;
		}
	}
	public boolean remove(T n){
		if(n == null)throw new IllegalArgumentException();
		if(root==null)return false;
		if(root.remove(n)){
			size--;
			version++;
			if(root!=null) {
				root.color=Color.BLACK;
			}
			return true;
		}
		if(root!=null) {
			root.color=Color.BLACK;
		}
		return false;
	}

	public int height(){
		if(root!=null){
			return root.height();
		}
		else{
			return -1;
		}
	}

	public boolean isEmpty(){
		if(root==null) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public String toString(){
		return this.toArrayList().toString();
	}

	public Object[] toArray(){
		return toArrayList().toArray();
	}

	public ArrayList<RedBlackTree.BinaryNode>  toArrayList(){
		ArrayList<RedBlackTree.BinaryNode>  a = new ArrayList<RedBlackTree.BinaryNode> ();
		
		if(root!=null) {
			Iterator<RedBlackTree.BinaryNode> i = iterator();
			while(i.hasNext()){
				a.add(i.next());
			}	
		}
		return a;
	}

	public Iterator<RedBlackTree.BinaryNode> iterator() {
		return new TreeIteratorPreorder(root);
	}
	
	public class BinaryNode{
		private T element;
		private BinaryNode leftChild;
		private BinaryNode rightChild;
		private Color color;
		
		public BinaryNode(T element){
			this.element = element;
			this.leftChild = null;
			this.rightChild = null;	
			this.color = Color.RED;
		}

		public int height(){
			int rightHeight = 0;
			int leftHeight = 0;
			if(rightChild!=null){
				rightHeight = rightChild.height()+1;
			}
			if(leftChild!=null){
				leftHeight = leftChild.height()+1;
			}
			if(rightHeight>leftHeight){
				return rightHeight;
			}
			else{
				return leftHeight;
			}
		}
		
		public Boolean remove(T n){
			if(this.leftChild==null&&this.rightChild==null){
				if(n.compareTo(this.element)==0){
					root=null;
					return true;
				}
				return false;
			}
			if(this.leftChild==null){
				if(n.compareTo(this.element)==0){
					root = this.rightChild;
					return true;
				}
				if(n.compareTo(this.rightChild.element)==0){
					root.rightChild=null;
					return true;
				}
				return false;
			}
			if(this.rightChild==null){
				if(n.compareTo(this.element)==0){
					root=this.leftChild;
					return true;
				}
				if(n.compareTo(this.leftChild.element)==0){
					root.leftChild=null;
					return true;
				}
				return false;
			}
			if(leftChild.color==Color.BLACK&&rightChild.color==Color.BLACK){
				this.color=Color.RED;
				int compare = n.compareTo(this.element);
				if(compare==0)return this.removeStep3(n, null, null);
				else if(compare > 0 && this.rightChild!=null)return this.rightChild.removeStep2(n, this, null);
				else if(compare < 0 && this.leftChild!=null)return this.leftChild.removeStep2(n, this, null);
				else return false;
			}
			else return this.removeStep2B(n, null, null);
		}

		private boolean removeStep2(T n, BinaryNode P, BinaryNode GP){
			if(this.leftChild!=null&&this.leftChild.color==Color.RED) return this.removeStep2B(n, P, GP);
			if(this.rightChild!=null&&this.rightChild.color==Color.RED) return this.removeStep2B(n, P, GP);
			return this.removeStep2A(n, P, GP);
		}
		private boolean removeStep2A(T n, BinaryNode P, BinaryNode GP){
			if(P.rightChild.element.compareTo(this.element)==0) {
				if(P.leftChild.leftChild!=null&&P.leftChild.leftChild.color==Color.RED)return this.removeStep2A3(n, P, GP);
				if(P.leftChild.rightChild!=null&&P.leftChild.rightChild.color==Color.RED)return this.removeStep2A2(n, P, GP);
				return this.removeStep2A1(n, P, GP);
			}
			else{
				if(P.rightChild.rightChild!=null&&P.rightChild.rightChild.color==Color.RED)return this.removeStep2A3(n, P, GP);
				if(P.rightChild.leftChild!=null&&P.rightChild.leftChild.color==Color.RED)return this.removeStep2A2(n, P, GP);
				return this.removeStep2A1(n, P, GP);
			}
		}
		private boolean removeStep2A1(T n, BinaryNode P, BinaryNode GP){
			P.color=Color.BLACK;
			if(P.rightChild!=null)P.rightChild.color=Color.RED;
			if(P.leftChild!=null)P.leftChild.color=Color.RED;
			int compare = n.compareTo(this.element);
			if(compare==0)return this.removeStep3(n, P, GP);
			else if(compare > 0 && this.rightChild!=null)return this.rightChild.removeStep2(n, this, P);
			else if(compare < 0 && this.leftChild!=null)return this.leftChild.removeStep2(n, this, P);
			else return false;
		}
		private boolean removeStep2A2(T n, BinaryNode P, BinaryNode GP){
			BinaryNode z;
			if(P.rightChild!=null && this.element.compareTo(P.rightChild.element)==0) z = P.doubleRightRotation();
			else if(P.leftChild!=null && this.element.compareTo(P.leftChild.element)==0)z = P.doubleLeftRotation();
			else z=P;
			if(GP==null)root=z;
			else if(P.element.compareTo(GP.rightChild.element)==0) GP.rightChild = z;
			else GP.leftChild = z;
			this.color=Color.RED;
			z.color=Color.RED;
			if(z.rightChild!=null)z.rightChild.color=Color.BLACK;
			if(z.leftChild!=null)z.leftChild.color=Color.BLACK;
			int compare = n.compareTo(this.element);
			if(compare==0)return this.removeStep3(n, P, GP);
			else if(compare > 0 && this.rightChild!=null)return this.rightChild.removeStep2(n, this, P);
			else if(compare < 0 && this.leftChild!=null) return this.leftChild.removeStep2(n, this, P);
			else return false;
		}
		private boolean removeStep2A3(T n, BinaryNode P, BinaryNode GP){
			BinaryNode z;
			if(P.rightChild!=null && this.element.compareTo(P.rightChild.element)==0) z = P.singleRightRotation();
			else if(P.leftChild!=null && this.element.compareTo(P.leftChild.element)==0) z = P.singleLeftRotation();
			else z = P;
			if(GP==null)root=z;
			else if(GP.rightChild!=null && P.element.compareTo(GP.rightChild.element)==0) GP.rightChild = z;
			else GP.leftChild = z;
			this.color= Color.RED;
			z.color = Color.RED;
			if(z.leftChild!=null)z.leftChild.color = Color.BLACK;
			if(z.rightChild!=null)z.rightChild.color = Color.BLACK;
			int compare = n.compareTo(this.element);
			if(compare==0)return this.removeStep3(n, P, GP);
			else if(compare > 0 && this.rightChild!=null)return this.rightChild.removeStep2(n, this, P);
			else if(compare < 0 && this.leftChild!=null)return this.leftChild.removeStep2(n, this, P);
			else return false;
		}
		private boolean removeStep2B(T n, BinaryNode P, BinaryNode GP){
			int compare = n.compareTo(this.element);
			if(compare==0)return this.removeStep3(n, P, GP);
			BinaryNode b;
			if(compare > 0)b = this.rightChild;
			else b=this.leftChild;
			if(b==null)return false;
			if(b.color==Color.RED)return b.removeStep2B1(n, this, P);
			else return b.removeStep2B2(n, this, P);
		}
		private boolean removeStep2B1(T n, BinaryNode P, BinaryNode GP){
			int compare = n.compareTo(this.element);
			if(compare==0)return this.removeStep3(n, P, GP);
			else if(compare > 0 && this.rightChild!=null)return this.rightChild.removeStep2(n, this, P);
			else if(compare < 0 && this.leftChild!=null) return this.leftChild.removeStep2(n, this, P);
			else return false;
		}
		private boolean removeStep2B2(T n, BinaryNode P, BinaryNode GP){
			BinaryNode b;
			if(P.rightChild!=null && P.rightChild.element.compareTo(this.element)==0) b = P.singleRightRotation();
			else b = P.singleLeftRotation();
			if(GP==null)root = b;
			else if(GP.rightChild!=null && P.element.compareTo(GP.rightChild.element)==0) GP.rightChild = b;
			else GP.leftChild = b;
			b.color = Color.BLACK;
			if(b.rightChild!=null)b.rightChild.color=Color.BLACK;
			if(b.leftChild!=null)b.leftChild.color=Color.BLACK;
			P.color = Color.RED;
			return this.removeStep2(n, P, GP);
		}
		private boolean removeStep3(T n, BinaryNode P, BinaryNode GP){
			if(this.rightChild!=null&&this.leftChild!=null){
				BinaryNode v = this.leftChild;
				while(v.rightChild!=null){
					v=v.rightChild;
				}
				if(this.color==Color.RED){
					this.element= v.element;
					leftChild.removeStep2(v.element, this, P);
					return true;
				}
				else{
					T temp = v.element;
					this.removeStep2B(v.element,P, GP);
					this.element = v.element;
					return true;
				}
			}
			else if(this.rightChild==null&&this.leftChild==null){
				if(P.rightChild!=null&&this.element.compareTo(P.rightChild.element)==0)P.rightChild=null;
				else P.leftChild=null;
				return true;
			}
			if(this.rightChild!=null){
				this.rightChild.color=Color.BLACK;
				if(P.rightChild!=null&&this.element.compareTo(P.rightChild.element)==0)P.rightChild=this.rightChild;
				else P.leftChild=this.rightChild;
				return true;
			}
			else{
				this.leftChild.color=Color.BLACK;
				if(P.rightChild!=null&&this.element.compareTo(P.rightChild.element)==0)P.rightChild=this.leftChild;
				else P.leftChild=this.leftChild;
				return true;
			}
		}
		
		
		private BinaryNode singleLeftRotation(){
			BinaryNode n = rightChild;
			rightChild = rightChild.leftChild;
			n.leftChild = this;
			rotationCount++;
			n.color=Color.BLACK;
			if(rightChild!=null)n.rightChild.color=Color.RED;
			n.leftChild.color=Color.RED;
			return n;
		}
		private BinaryNode singleRightRotation() {
			BinaryNode n = leftChild;
			leftChild = leftChild.rightChild;
			n.rightChild = this;
			rotationCount++;
			n.color=Color.BLACK;
			n.rightChild.color=Color.RED;
			if(leftChild!=null)n.leftChild.color=Color.RED;
			return n;
		}
		
		public BinaryNode doubleRightRotation() {
			doubleRotationCount++;
			if(leftChild!=null) {
				leftChild = leftChild.singleLeftRotation();
				if(leftChild.rightChild!=null)leftChild.rightChild.color=Color.BLACK;
			}
			return this.singleRightRotation();
		}

		public BinaryNode doubleLeftRotation() {
			doubleRotationCount++;
			if(rightChild!=null) {
				rightChild = rightChild.singleRightRotation();
				if(rightChild.leftChild!=null)rightChild.leftChild.color=Color.BLACK;
			}
			return this.singleLeftRotation();
		}

		public T getElement() {
			return this.element;
		}

		public Color getColor() {
			return this.color;
		}

		public BinaryNode getLeftChild() {
			return leftChild;
		}
		
		public BinaryNode getRightChild() {
			return rightChild;
		}
	}

	private class TreeIteratorPreorder implements Iterator<RedBlackTree.BinaryNode>{

	    private Stack<BinaryNode> s;
	    private int versionPO;
	    private BinaryNode current;
		
		public TreeIteratorPreorder(BinaryNode n){
	        s = new Stack<BinaryNode>();
	        if(n!=null)s.push(n);
	        versionPO = version;
	        current = null;
		}

	    public boolean hasNext(){
	        return !s.isEmpty();
	    }

	    public RedBlackTree.BinaryNode next(){
	        if(!this.hasNext()) throw new NoSuchElementException();
	        if(versionPO!=version)throw new ConcurrentModificationException();  
	        
	        current = s.pop();

	        if(current.rightChild != null) s.push(current.rightChild);
	        if(current.leftChild != null) s.push(current.leftChild);

	        return current;
	    }
	    
	    public void remove() {
	    	if(versionPO!=version)throw new ConcurrentModificationException();
	    	if(current==null)throw new IllegalStateException();
	    	
	    	RedBlackTree.this.remove(current.element);
	    	
	    	if(current.leftChild != null && current.rightChild != null)s.push(current);
	    	
	    	current = null;
	    	versionPO++;
	    }
	}

	private class MyBoolean {

		private boolean value;
		
		public MyBoolean() {
			value = false;
		}
	}
	
}

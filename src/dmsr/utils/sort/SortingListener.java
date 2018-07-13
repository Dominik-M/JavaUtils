package dmsr.utils.sort;

public interface SortingListener {
    
    public void accessed(int i);
    
    public void compared(int i,int j);
    
    public void switched(int i,int j);
    
}
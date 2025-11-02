package com.example.demo.service.impl;  


// 引入需要的類別
import com.example.demo.dao.CustomerDAO;  // 客戶 DAO
import com.example.demo.dao.OrderDAO;  // 訂單 DAO
import com.example.demo.model.*;  // 引入 model 套件的所有類別
import com.example.demo.service.CartService;  // 購物車 Service
import com.example.demo.service.OrderService;  // 訂單 Service 介面
import org.springframework.beans.factory.annotation.Autowired;  // Spring 自動注入註解
import org.springframework.stereotype.Service;  // Spring Service 註解
import org.springframework.transaction.annotation.Transactional;  // Spring 事務註解
import java.util.Date;  // Date 類別
import java.util.List;  // List 介面

/**
 * 訂單 Service 實作類別
 * 實作 OrderService 介面定義的所有方法
 * 處理訂單相關的業務邏輯
 */
@Service  // 標示這是一個 Service（業務邏輯層）元件，Spring 會自動掃描並註冊
@Transactional  // 標示此類別的所有方法都在事務（Transaction）中執行
// 事務的特性：要嘛全部成功，要嘛全部失敗（原子性）
// 例如：建立訂單時，如果清空購物車失敗，訂單也不會被建立
public class OrderServiceImpl implements OrderService {  // 實作 OrderService 介面
    
    // ========== 依賴注入 ==========
    // 使用 @Autowired 讓 Spring 自動注入需要的物件
    
    @Autowired  // 自動注入訂單 DAO
    private OrderDAO orderDAO;
    
    @Autowired  // 自動注入客戶 DAO
    private CustomerDAO customerDAO;
    
    @Autowired  // 自動注入購物車 Service
    private CartService cartService;
    
    // ========== 實作 Service 方法 ==========
    
    /**
     * 從購物車建立訂單
     * 這是最核心的業務方法，實現「結帳」功能
     * @param customerId 客戶 ID
     * @param recipientName 收貨人姓名
     * @param recipientPhone 收貨人電話
     * @param shippingAddress 收貨地址
     * @param orderNote 訂單備註
     * @return 建立完成的訂單物件
     */
    @Override  // 標示此方法是覆寫介面的方法
    public Order createOrderFromCart(Long customerId, String recipientName, 
                                    String recipientPhone, String shippingAddress, 
                                    String orderNote) {
        
        // ===== 步驟1：驗證客戶是否存在 =====
        // 使用 customerDAO 根據 ID 查詢客戶
        Customer customer = customerDAO.findById(customerId);
        
        // 檢查客戶是否存在
        if (customer == null) {  // 如果客戶不存在
            // 拋出執行時期例外（RuntimeException），中斷程式執行
            throw new RuntimeException("找不到客戶，ID: " + customerId);
        }
        
        // ===== 步驟2：取得購物車並檢查 =====
        // 使用 cartService 取得客戶的購物車
        Cart cart = cartService.getCartByCustomerId(customerId);
        
        // 檢查購物車是否為空
        // cart == null：購物車不存在
        // cart.getCartItems().isEmpty()：購物車存在但沒有商品
        if (cart == null || cart.getCartItems().isEmpty()) {
            // 拋出例外，不能從空購物車建立訂單
            throw new RuntimeException("購物車是空的，無法建立訂單");
        }
        
        // ===== 步驟3：建立訂單物件 =====
        // 使用有參數的建構子建立訂單
        // 參數依序為：客戶、收貨人姓名、收貨人電話、收貨地址
        Order order = new Order(customer, recipientName, recipientPhone, shippingAddress);
        
        // 設定訂單備註
        order.setOrderNote(orderNote);
        
        // ===== 步驟4：將購物車項目複製到訂單項目 =====
        // 使用 for-each 迴圈遍歷購物車中的每個項目
        for (CartItem cartItem : cart.getCartItems()) {
            // 為每個購物車項目建立對應的訂單項目
            // OrderItem 建構子參數：產品物件、購買數量
            OrderItem orderItem = new OrderItem(
                cartItem.getProduct(),   // 取得購物車項目的產品
                cartItem.getQuantity()   // 取得購物車項目的數量
            );
            
            // 將訂單項目加入到訂單中
            // addOrderItem() 會建立雙向關聯
            order.addOrderItem(orderItem);
        }
        
        // ===== 步驟5：計算訂單總金額 =====
        // 呼叫 Order 類別的 calculateTotalAmount() 方法
        // 此方法會遍歷所有訂單項目，累加小計得到總金額
        order.calculateTotalAmount();
        
        // ===== 步驟6：儲存訂單到資料庫 =====
        // 使用 orderDAO 儲存訂單
        // 由於設定了 cascade，訂單項目也會一起儲存
        orderDAO.save(order);
        
        // ===== 步驟7：清空購物車 =====
        // 訂單建立成功後，清空客戶的購物車
        // 避免重複下訂
        cartService.clearCart(customerId);
        
        // ===== 步驟8：回傳建立的訂單 =====
        return order;
    }
    
    /**
     * 根據 ID 查詢訂單
     * @param orderId 訂單 ID
     * @return 訂單物件
     */
    @Override
    public Order getOrderById(Long orderId) {
        // 使用 orderDAO 查詢訂單
        Order order = orderDAO.findById(orderId);
        
        // 檢查訂單是否存在
        if (order == null) {  // 如果訂單不存在
            // 拋出例外
            throw new RuntimeException("找不到訂單，ID: " + orderId);
        }
        
        // 回傳訂單物件
        return order;
    }
    
    /**
     * 根據訂單編號查詢
     * @param orderNumber 訂單編號
     * @return 訂單物件
     */
    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        // 使用 orderDAO 根據訂單編號查詢
        Order order = orderDAO.findByOrderNumber(orderNumber);
        
        // 檢查訂單是否存在
        if (order == null) {
            // 拋出例外
            throw new RuntimeException("找不到訂單，訂單編號: " + orderNumber);
        }
        
        // 回傳訂單物件
        return order;
    }
    
    /**
     * 查詢所有訂單
     * @return 所有訂單的 List
     */
    @Override
    public List<Order> getAllOrders() {
        // 直接呼叫 orderDAO 的 findAll() 方法
        // 回傳所有訂單的 List
        return orderDAO.findAll();
    }
    
    /**
     * 查詢客戶的所有訂單
     * @param customerId 客戶 ID
     * @return 該客戶的所有訂單 List
     */
    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        // 先查詢客戶是否存在
        Customer customer = customerDAO.findById(customerId);
        
        // 檢查客戶
        if (customer == null) {
            throw new RuntimeException("找不到客戶，ID: " + customerId);
        }
        
        // 使用 orderDAO 查詢該客戶的所有訂單
        return orderDAO.findByCustomer(customer);
    }
    
    /**
     * 根據狀態查詢訂單
     * @param status 訂單狀態枚舉
     * @return 符合該狀態的所有訂單 List
     */
    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        // 直接呼叫 orderDAO 的 findByStatus() 方法
        return orderDAO.findByStatus(status);
    }
    
    /**
     * 更新訂單狀態
     * @param orderId 訂單 ID
     * @param status 新的訂單狀態（字串格式，例如："PAID", "PROCESSING"）
     */
    @Override
    public void updateOrderStatus(Long orderId, String status) {
        System.out.println("=== 更新訂單狀態 ===");
        System.out.println("訂單 ID: " + orderId);
        System.out.println("新狀態: " + status);
        
        // 先查詢訂單
        Order order = getOrderById(orderId);
        
        try {
            // 將字串轉換為 OrderStatus 枚舉
            OrderStatus newStatus = OrderStatus.valueOf(status);
            
            System.out.println("原始狀態: " + order.getOrderStatus());
            System.out.println("新狀態: " + newStatus);
            
            // 更新訂單狀態
            order.setOrderStatus(newStatus);
            
            // 更新修改時間為當前時間
            order.setUpdatedDate(new Date());
            
            // 儲存訂單
            orderDAO.save(order);
            
            System.out.println("✓ 訂單狀態更新成功");
            
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 無效的訂單狀態: " + status);
            throw new RuntimeException("無效的訂單狀態：" + status);
        }
    }
    
    /**
     * 取消訂單
     * @param orderId 訂單 ID
     */
    @Override
    public void cancelOrder(Long orderId) {
        // 先查詢訂單
        Order order = getOrderById(orderId);
        
        // ===== 檢查訂單是否可以取消 =====
        
        // 檢查：如果訂單已出貨或已送達，不能取消
        if (order.getOrderStatus() == OrderStatus.SHIPPED ||   // 已出貨
            order.getOrderStatus() == OrderStatus.DELIVERED) {  // 已送達
            // 拋出例外
            throw new RuntimeException("訂單已出貨或送達，無法取消");
            }
            		// 如果訂單已經是取消狀態，不需要再取消
            	    if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            	        throw new RuntimeException("訂單已經取消");
            	    }
            	    
            	    // ===== 執行取消操作 =====
            	    
            	    // 更新訂單狀態為「已取消」
            	    order.setOrderStatus(OrderStatus.CANCELLED);
            	    
            	    // 更新修改時間為當前時間
            	    order.setUpdatedDate(new Date());
            	    
            	    // 儲存訂單
            	    orderDAO.save(order);
            	}
}
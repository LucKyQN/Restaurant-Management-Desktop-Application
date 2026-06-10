// Đảm bảo code chạy sau khi HTML đã tải xong
document.addEventListener('DOMContentLoaded', function () {

    // 1. Xử lý hiệu ứng cuộn cho Thanh điều hướng (Navbar)
    const navbar = document.getElementById('navbar');

    window.addEventListener('scroll', () => {
        if (window.scrollY > 80) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // 2. Xử lý sự kiện click nút Đặt bàn
    const bookingButtons = [
        document.getElementById('btn-booking-nav'),
        document.getElementById('btn-explore')
    ];

    bookingButtons.forEach(btn => {
        if (btn) {
            btn.addEventListener('click', () => {
                hienThiThongBao();
            });
        }
    });

    function hienThiThongBao() {
        // Bạn có thể thay bằng một cái Modal đẹp hơn sau này
        alert("🏮 Chào mừng bạn đến với Ngói Đỏ!\n\nHiện tại tính năng đặt bàn online đang bảo trì. Bạn vui lòng liên hệ Hotline 0123 456 789 để được hỗ trợ giữ chỗ nhanh nhất nhé.");
    }

    // 3. Hiệu ứng Fade-in đơn giản khi cuộn trang (Optional)
    const observerOptions = {
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);

    document.querySelectorAll('.section-title, .menu-item').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'all 0.6s ease-out';
        observer.observe(el);
    });
    // 4. Xử lý nút "Xem Tất Cả"
    const btnViewAll = document.getElementById('btn-view-all');
    const featuredMenu = document.getElementById('featured-menu');
    const fullMenu = document.getElementById('full-menu');
    const menuTitle = document.getElementById('menu-title');

    if (btnViewAll) {
        btnViewAll.addEventListener('click', function () {
            // Ẩn 3 món nổi bật, hiện toàn bộ thực đơn
            featuredMenu.classList.add('hidden');
            fullMenu.classList.remove('hidden');
            // Đổi tiêu đề
            menuTitle.innerText = "THỰC ĐƠN NHÀ HÀNG";
            // Kéo màn hình lên đầu khu vực thực đơn mượt mà
            document.getElementById('menu').scrollIntoView({ behavior: 'smooth' });
        });
    }

    // 5. Xử lý bộ lọc (Tabs)
    const filterBtns = document.querySelectorAll('.filter-btn');
    const menuItems = document.querySelectorAll('.full-item');

    filterBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            // Xóa class 'active' (màu đỏ) ở tất cả các nút
            filterBtns.forEach(b => b.classList.remove('active'));
            // Thêm class 'active' vào nút vừa được bấm
            btn.classList.add('active');

            // Lấy giá trị data-filter (ví dụ: 'do-uong', 'mon-chinh')
            const filterValue = btn.getAttribute('data-filter');

            // Duyệt qua từng món ăn để Ẩn/Hiện
            menuItems.forEach(item => {
                if (filterValue === 'all' || item.getAttribute('data-category') === filterValue) {
                    item.style.display = 'block'; // Hiện món
                } else {
                    item.style.display = 'none';  // Ẩn món
                }
            });
        });
    });
});
const express = require('express');
const { Pool } = require('pg'); // <-- Import PostgreSQL
const cors = require('cors');

const app = express();
const port = 3000;

const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'ChamSocSucKhoe-app',
  password: '26032004',
  port: 5432
});

app.use(express.json());
app.use(cors()); // Cho phép tất cả các nguồn gốc

app.get('/api/benh-co-ban', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM benh_co_ban ORDER BY ngay_tao DESC');
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: 'Lỗi lấy danh sách bệnh cơ bản' });
  }
});

// POST thêm bệnh mới
app.post('/api/benh-co-ban', async (req, res) => {
  const { ten, trieu_chung, nguyen_nhan, cach_dieu_tri, url_anh } = req.body;
  try {
    const result = await pool.query(
      'INSERT INTO benh_co_ban (ten, trieu_chung, nguyen_nhan, cach_dieu_tri, url_anh, ngay_tao) VALUES ($1, $2, $3, $4, $5, NOW()) RETURNING *',
      [ten, trieu_chung, nguyen_nhan, cach_dieu_tri, url_anh]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: 'Lỗi thêm bệnh cơ bản' });
  }
});

// ======== DINH DUONG =========

// GET tất cả dinh dưỡng
app.get('/api/dinh-duong', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM dinh_duong ORDER BY ngay_tao DESC');
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: 'Lỗi lấy danh sách dinh dưỡng' });
  }
});

// POST thêm dinh dưỡng
app.post('/api/dinh-duong', async (req, res) => {
  const { ten, noi_dung, url_anh } = req.body;
  try {
    const result = await pool.query(
      'INSERT INTO dinh_duong (ten, noi_dung, url_anh, ngay_tao) VALUES ($1, $2, $3, NOW()) RETURNING *',
      [ten, noi_dung, url_anh]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: 'Lỗi thêm nội dung dinh dưỡng' });
  }
});

// ======== SO CUU =========

// GET tất cả sơ cứu
app.get('/api/so-cuu', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM so_cuu ORDER BY ngay_tao DESC');
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: 'Lỗi lấy danh sách sơ cứu' });
  }
});

// POST thêm sơ cứu
app.post('/api/so-cuu', async (req, res) => {
  const { ten, trieu_chung, viec_nen_lam, viec_khong_nen_lam, cach_xu_ly, url_anh } = req.body;
  try {
    const result = await pool.query(
      'INSERT INTO so_cuu (ten, trieu_chung, viec_nen_lam, viec_khong_nen_lam, cach_xu_ly, url_anh, ngay_tao) VALUES ($1, $2, $3, $4, $5, $6, NOW()) RETURNING *',
      [ten, trieu_chung, viec_nen_lam, viec_khong_nen_lam, cach_xu_ly, url_anh]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: 'Lỗi thêm thông tin sơ cứu' });
  }
});

// ======== SERVER =========
app.listen(port, () => {
  console.log(`✅ Server đang chạy tại http://localhost:${port}`);
});
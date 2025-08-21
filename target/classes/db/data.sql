-- 사용자 데이터
INSERT INTO users (id, email, name, password_hash, role, status, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin@cctv-ai.com', '관리자', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 카메라 데이터
INSERT INTO cameras (id, user_id, name, lat, lng, status, stream_url, rtsp_url, created_at, updated_at) VALUES
('cam-001', '550e8400-e29b-41d4-a716-446655440001', '세집매 삼거리', 36.8625719, 127.1504447, 'ONLINE', 'http://localhost:5001/stream/cam-001', 'rtsp://210.99.70.120:1935/live/cctv001.stream', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cam-002', '550e8400-e29b-41d4-a716-446655440001', '서부역 입구 삼거리', 36.8105742, 127.1409331, 'ONLINE', 'http://localhost:5001/stream/cam-002', 'rtsp://210.99.70.120:1935/live/cctv002.stream', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cam-003', '550e8400-e29b-41d4-a716-446655440001', '역말 오거리', 36.8249888, 127.1531387, 'ONLINE', 'http://localhost:5001/stream/cam-003', 'rtsp://210.99.70.120:1935/live/cctv003.stream', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cam-004', '550e8400-e29b-41d4-a716-446655440001', '천안로사거리', 36.8218382, 127.1625436, 'ONLINE', 'http://localhost:5001/stream/cam-004', 'rtsp://210.99.70.120:1935/live/cctv004.stream', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

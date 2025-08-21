-- 사용자 데이터
INSERT INTO users (id, email, name, password_hash, role, status, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin@cctv-ai.com', '관리자', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 기본 카메라 데이터 (cam-001, cam-002는 YOLO 활성화)
INSERT INTO cameras (id, user_id, name, lat, lng, status, stream_url, rtsp_url, yolo_enabled, created_at, updated_at) VALUES
('cam-001', '550e8400-e29b-41d4-a716-446655440001', '세집매 삼거리', 36.8625719, 127.1504447, 'ONLINE', 'http://detector:5001/stream/cam-001', 'rtsp://210.99.70.120:1935/live/cctv001.stream', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cam-002', '550e8400-e29b-41d4-a716-446655440001', '서부역 입구 삼거리', 36.8105742, 127.1409331, 'ONLINE', 'http://detector:5001/stream/cam-002', 'rtsp://210.99.70.120:1935/live/cctv002.stream', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

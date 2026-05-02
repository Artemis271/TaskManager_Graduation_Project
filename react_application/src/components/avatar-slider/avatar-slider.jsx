import React, {useRef, useState} from 'react';
import {useAuth} from '../../hooks/auth';
import {useParams} from 'react-router-dom';
import {Swiper, SwiperSlide} from 'swiper/react';
import {FaAngleLeft, FaAngleRight} from 'react-icons/fa';
import 'swiper/css';
import 'swiper/css/navigation';
import './avatar-slider.css';
import {PiUploadSimpleBold} from "react-icons/pi";
import axios from "axios";
import ModalWindow from "../modal-window/modal-window";
import AvatarImage from "../avatar-image/avatar-image";

export default function AvatarSlider({ data, baseUrl })
{
    const { userId } = useParams();
    const { user } = useAuth();
    const swiperRef = useRef(null);
    const [current, setCurrent] = useState(0);

    const avatars = Array.isArray(data) && data.length
        ? [...data].sort((a, b) => b.dateAdded.localeCompare(a.dateAdded))
        : [{ contentType: 'image/png', binaryData: null }];

    const isOwner = user && String(user.id) === userId;

    const saveAvatar = async (e) => {
        const {files} = e.target
        if (files[0].size !== 0 && files[0].size <= 5 * 1024 * 1024) {
            const payload = new FormData();
            payload.append('file', files[0])
            await axios.patch(baseUrl, payload, {headers: {'Content-Type': 'multipart/form-data'}})
            window.location.reload();
        }
    }

    return (
        <div className="avatar-slider-container">
            {avatars.length > 1 && <button
                className="slider-arrow prev-arrow"
                onClick={() => swiperRef.current?.slidePrev()}
                disabled={current === 0}
                aria-label="Previous avatar"><FaAngleLeft/>
            </button>}

            <Swiper
                slidesPerView={1}
                spaceBetween={10}
                onSwiper={swiper => (swiperRef.current = swiper)}
                onSlideChange={({ activeIndex }) => setCurrent(activeIndex)}>
                {avatars.map((av, idx) => (
                    <SwiperSlide key={idx}>
                        <ModalWindow
                            style={{ padding: "0", display: "flex" }}
                            trigger={<AvatarImage av={av} idx={idx} className="avatar-link" />}>
                            {({ close }) => <AvatarImage av={av} idx={idx} className="image-user-avatar" />}
                        </ModalWindow>
                    </SwiperSlide>
                ))}
                {isOwner && (
                    <div className="avatar-upload">
                        <input
                            type="file"
                            id="avatar-upload-input"
                            onChange={saveAvatar}
                        />
                        <label htmlFor="avatar-upload-input"><PiUploadSimpleBold /></label>
                    </div>
                )}
            </Swiper>

            {avatars.length > 1 && <button
                className="slider-arrow next-arrow"
                onClick={() => swiperRef.current?.slideNext()}
                disabled={current === avatars.length - 1}
                aria-label="Next avatar"><FaAngleRight/>
            </button>}
        </div>
    );
}
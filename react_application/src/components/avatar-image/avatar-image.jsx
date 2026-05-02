export default function AvatarImage({ av, idx, className, ...props }) {
    const { src, alt } = av.binaryData
        ? { src: `data:${av.contentType};base64,${av.binaryData}`, alt: `avatar-${idx}` }
        : { src: "/default-avatar.png", alt: "default avatar" };

    return <img className={className} src={src} alt={alt} {...props} />;
}
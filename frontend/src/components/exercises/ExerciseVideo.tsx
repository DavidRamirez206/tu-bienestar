import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import Typography from "@mui/material/Typography";

const YOUTUBE_ID = /(?:youtube\.com\/(?:watch\?(?:.*&)?v=|embed\/|shorts\/)|youtu\.be\/)([\w-]{11})/;
const DIRECT_VIDEO = /\.(mp4|webm|ogg)(\?.*)?$/i;

type Props = {
  url?: string;
  title: string;
};

const ExerciseVideo = ({ url, title }: Props) => {
  if (!url) {
    return (
      <Typography variant="body2" color="text.secondary">
        Este ejercicio no tiene video.
      </Typography>
    );
  }

  const youtubeId = YOUTUBE_ID.exec(url)?.[1];
  if (youtubeId) {
    return (
      <Box
        component="iframe"
        src={`https://www.youtube-nocookie.com/embed/${youtubeId}`}
        title={`Video de ${title}`}
        allow="accelerometer; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        referrerPolicy="strict-origin-when-cross-origin"
        allowFullScreen
        sx={{ width: "100%", aspectRatio: "16 / 9", border: 0, borderRadius: 2, display: "block" }}
      />
    );
  }

  if (DIRECT_VIDEO.test(url)) {
    return (
      <Box
        component="video"
        controls
        src={url}
        sx={{ width: "100%", maxHeight: 420, borderRadius: 2, display: "block", bgcolor: "black" }}
      />
    );
  }

  return (
    <Link href={url} target="_blank" rel="noopener noreferrer">
      Abrir el video en otra pestaña
    </Link>
  );
};

export default ExerciseVideo;

import Card from "@mui/material/Card";
import CardActionArea from "@mui/material/CardActionArea";
import CardContent from "@mui/material/CardContent";
import Stack from "@mui/material/Stack";
import type { ReactNode } from "react";

type Props = {
  onClick?: () => void;
  header?: ReactNode;
  children: ReactNode;
};

const EntityCard = ({ onClick, header, children }: Props) => {
  const CardContentWrapper = (
    <CardContent sx={{ width: "100%", p: 2.5 }}>
      <Stack spacing={1.5} sx={{ height: "100%" }}>
        {header}
        {children}
      </Stack>
    </CardContent>
  );

  return (
    <Card
      sx={{
        height: "100%",
        borderRadius: 3,
        boxShadow: 2,
        border: "1px solid",
        borderColor: "divider",
        transition: "all 0.2s ease",
        display: "flex",
        "&:hover": {
          boxShadow: 5,
          transform: "translateY(-2px)",
        },
      }}
    >
      {onClick ? (
        <CardActionArea
          onClick={onClick}
          sx={{
            height: "100%",
            display: "flex",
            alignItems: "stretch",
          }}
        >
          {CardContentWrapper}
        </CardActionArea>
      ) : (
        CardContentWrapper
      )}
    </Card>
  );
};

export default EntityCard;

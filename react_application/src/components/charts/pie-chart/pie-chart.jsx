import {Pie} from "react-chartjs-2";
import {ArcElement, Chart, Legend, Tooltip} from "chart.js";

Chart.register(Tooltip, Legend, ArcElement)

export default function PieChart({label, data, labels})
{
    const generateRandomColor = (count, transparency = 0.7) => {
        const colors = [];
        for (let i = 0; i < count; i++) {
            const hue = (i * 360 / count) % 360;
            colors.push(`hsla(${hue}, 70%, 50%, ${transparency})`);
        }
        return colors;
    }

    const dataChart = {
        labels: labels,
        datasets: [
            {
                label: label,
                data: data,
                backgroundColor: generateRandomColor(data.length),
                borderWidth: 1,
                borderColor: generateRandomColor(data.length, 1),
                hoverOffset: 4
            }
        ]
    }

    return(<Pie data={dataChart}/>)
}
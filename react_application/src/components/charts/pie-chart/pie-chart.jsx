import {Pie} from "react-chartjs-2";
import {ArcElement, Chart, Legend, Tooltip} from "chart.js";

Chart.register(Tooltip, Legend, ArcElement)

const PALETTE = [
    'rgba(124, 58, 237, 0.75)',
    'rgba(167, 139, 250, 0.75)',
    'rgba(109, 40, 217, 0.75)',
    'rgba(196, 181, 253, 0.75)',
    'rgba(79, 70, 229, 0.75)',
    'rgba(139, 92, 246, 0.75)',
    'rgba(221, 214, 254, 0.75)',
    'rgba(55, 48, 163, 0.75)',
];

const PALETTE_BORDER = PALETTE.map(c => c.replace('0.75', '1'));

export default function PieChart({label, data, labels})
{
    const bg     = data.map((_, i) => PALETTE[i % PALETTE.length]);
    const border = data.map((_, i) => PALETTE_BORDER[i % PALETTE_BORDER.length]);

    const dataChart = {
        labels,
        datasets: [{
            label,
            data,
            backgroundColor: bg,
            borderColor: border,
            borderWidth: 1,
            hoverOffset: 6,
        }]
    };

    const options = {
        plugins: {
            legend: {
                labels: { color: '#6b7280', font: { size: 12 } }
            }
        }
    };

    return <Pie data={dataChart} options={options}/>;
}

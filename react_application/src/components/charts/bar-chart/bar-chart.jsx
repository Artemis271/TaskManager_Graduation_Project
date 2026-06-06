import { Bar } from 'react-chartjs-2';
import {
    BarElement, CategoryScale, Chart as ChartJS,
    Legend, LinearScale, Title, Tooltip,
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const PALETTE = [
    'rgba(124, 58, 237, 0.75)',
    'rgba(167, 139, 250, 0.75)',
    'rgba(109, 40, 217, 0.75)',
    'rgba(196, 181, 253, 0.75)',
    'rgba(79, 70, 229, 0.75)',
];

const PALETTE_BORDER = PALETTE.map(c => c.replace('0.75', '1'));

export default function BarChart({ label, data, labels })
{
    const bg     = labels.map((_, i) => PALETTE[i % PALETTE.length]);
    const border = labels.map((_, i) => PALETTE_BORDER[i % PALETTE_BORDER.length]);

    const options = {
        scales: {
            y: {
                beginAtZero: true,
                ticks: { stepSize: 1, color: '#9ca3af' },
                grid: { color: 'rgba(124, 58, 237, 0.08)' },
            },
            x: {
                ticks: { color: '#9ca3af' },
                grid: { display: false },
            },
        },
        plugins: {
            legend: { labels: { color: '#6b7280', font: { size: 12 } } }
        }
    };

    const chartData = {
        labels,
        datasets: [{
            label,
            data,
            backgroundColor: bg,
            borderColor: border,
            borderWidth: 1,
            borderRadius: 6,
            hoverOffset: 4,
        }],
    };

    return <Bar options={options} data={chartData}/>;
}

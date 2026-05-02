import { Bar } from 'react-chartjs-2';
import {
    BarElement,
    CategoryScale,
    Chart as ChartJS,
    Legend,
    LinearScale,
    Title,
    Tooltip,
} from 'chart.js';

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
);

export default function BarChart({ label, data, labels })
{
    const generateRandomHues = (count) =>
        Array.from({ length: count }, () => Math.floor(Math.random() * 360));

    const hues = generateRandomHues(labels.length);
    const backgroundColors = hues.map(hue => `hsla(${hue}, 70%, 50%, 0.7)`);
    const borderColors = hues.map(hue => `hsla(${hue}, 70%, 50%, 1)`);

    const options = {
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    stepSize: 1,
                },
            },
        },
    };

    const chartData = {
        labels: labels,
        datasets: [
            {
                label: label,
                data: data,
                backgroundColor: backgroundColors,
                borderWidth: 1,
                borderColor: borderColors,
                hoverOffset: 4
            }
        ],
    };

    return <Bar options={options} data={chartData} />;
}
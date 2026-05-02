import {Line} from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);


export default function LineChart({ label, data, labels })
{
    const hue = Math.floor(Math.random() * 360);
    const bgColor     = `hsla(${hue}, 70%, 50%, 0.7)`;
    const borderColor = `hsla(${hue}, 70%, 50%, 1)`;

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
                backgroundColor: bgColor,
                borderWidth: 3,
                borderColor: borderColor,
                hoverOffset: 4,
                tension: 0.1,
                pointBackgroundColor: 'rgb(255,255,255)',
                pointBorderColor: 'rgb(52,52,52)',
            }
        ],
    };

    return <Line options={options} data={chartData} />;
}
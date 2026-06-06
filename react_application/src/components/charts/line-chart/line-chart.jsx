import {Line} from 'react-chartjs-2';
import {
    Chart as ChartJS, CategoryScale, LinearScale,
    PointElement, LineElement, Title, Tooltip, Legend, Filler,
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, Filler);

export default function LineChart({ label, data, labels })
{
    const options = {
        scales: {
            y: {
                beginAtZero: true,
                ticks: { stepSize: 1, color: '#9ca3af' },
                grid: { color: 'rgba(124, 58, 237, 0.08)' },
            },
            x: {
                ticks: { color: '#9ca3af', maxTicksLimit: 10 },
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
            backgroundColor: 'rgba(124, 58, 237, 0.12)',
            borderColor: 'rgba(124, 58, 237, 0.9)',
            borderWidth: 2.5,
            tension: 0.35,
            fill: true,
            pointBackgroundColor: '#fff',
            pointBorderColor: 'rgba(124, 58, 237, 0.9)',
            pointRadius: 4,
            pointHoverRadius: 6,
        }],
    };

    return <Line options={options} data={chartData}/>;
}
